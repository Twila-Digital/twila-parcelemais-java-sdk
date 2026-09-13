package twila.parcelemais.internal.resilience;

import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.errors.ParceleMaisException;
import twila.parcelemais.errors.ParceleMaisTimeoutException;
import twila.parcelemais.internal.http.ApiResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalBiFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public final class ResilienceExecutor implements AutoCloseable {

    private final ParceleMaisResilienceOptions options;
    private final CircuitBreaker circuitBreaker;
    private final ExecutorService executor;

    public ResilienceExecutor(ParceleMaisResilienceOptions options) {
        this.options = options;
        this.circuitBreaker = CircuitBreaker.of("parcelemais-api", buildCircuitBreakerConfig(options));
        this.executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "parcelemais-resilience");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public ApiResponse execute(boolean retrySafe, Callable<ApiResponse> attempt) {
        Retry retry = buildRetry(retrySafe);
        Supplier<ApiResponse> decorated = Retry.decorateSupplier(retry, CircuitBreaker.decorateSupplier(circuitBreaker, wrap(attempt)));
        Callable<ApiResponse> task = decorated::get;

        Future<ApiResponse> future = executor.submit(task);
        try {
            return future.get(options.getTotalTimeout().toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new ParceleMaisTimeoutException("A requisição excedeu o tempo limite configurado.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ParceleMaisTimeoutException("A requisição foi interrompida.", ex);
        } catch (ExecutionException ex) {
            throw unwrap(ex.getCause());
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    private static RuntimeException unwrap(Throwable cause) {
        if (cause instanceof CallNotPermittedException)
            return new ParceleMaisTimeoutException("O circuit breaker está aberto — chamadas recentes falharam de forma consistente.", cause);

        if (cause instanceof HttpAttemptException)
            return new ParceleMaisTimeoutException("Falha de rede ao chamar a API do Parcele+.", cause.getCause());

        if (cause instanceof ParceleMaisException)
            return (ParceleMaisException) cause;

        if (cause instanceof RuntimeException)
            return (RuntimeException) cause;

        return new ParceleMaisTimeoutException("Falha inesperada ao chamar a API do Parcele+.", cause);
    }

    @SuppressWarnings("unchecked")
    private Retry buildRetry(boolean retrySafe) {
        if (options.getMaxRetryAttempts() <= 1)
            return Retry.of(UUID.randomUUID().toString(), RetryConfig.custom().maxAttempts(1).build());

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(options.getMaxRetryAttempts())
                .intervalBiFunction(backoffWithRetryAfter())
                .retryOnResult(result -> retrySafe && result instanceof ApiResponse
                        && TransientFailureClassifier.isTransientResponse((ApiResponse) result, options))
                .retryOnException(ex -> retrySafe && TransientFailureClassifier.isTransientException(unwrapAttemptException(ex)))
                .build();

        return Retry.of(UUID.randomUUID().toString(), config);
    }

    private IntervalBiFunction<Object> backoffWithRetryAfter() {
        final long baseDelayMillis = options.getRetryBaseDelay().toMillis();

        return (attempt, either) -> {
            if (either.isRight() && either.get() instanceof ApiResponse) {
                String retryAfter = ((ApiResponse) either.get()).headers().get("Retry-After");
                if (retryAfter != null) {
                    try {
                        return TimeUnit.SECONDS.toMillis(Long.parseLong(retryAfter.trim()));
                    } catch (NumberFormatException ignored) {
                        // não é um Retry-After em segundos; cai para o backoff padrão abaixo.
                    }
                }
            }

            long exponential = baseDelayMillis * (1L << Math.max(0, attempt - 1));
            double jitterFactor = 0.5 + ThreadLocalRandom.current().nextDouble();
            return (long) (exponential * jitterFactor);
        };
    }

    @SuppressWarnings("unchecked")
    private static CircuitBreakerConfig buildCircuitBreakerConfig(ParceleMaisResilienceOptions options) {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold((float) (options.getCircuitBreakerFailureRatio() * 100))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(options.getCircuitBreakerSlidingWindowSize())
                .minimumNumberOfCalls(options.getCircuitBreakerMinimumThroughput())
                .waitDurationInOpenState(options.getCircuitBreakerBreakDuration())
                .recordResult(result -> result instanceof ApiResponse
                        && TransientFailureClassifier.isTransientResponse((ApiResponse) result, options))
                .recordException(ex -> TransientFailureClassifier.isTransientException(unwrapAttemptException(ex)))
                .build();
    }

    private static Throwable unwrapAttemptException(Throwable ex) {
        return ex instanceof HttpAttemptException ? ex.getCause() : ex;
    }

    private static Supplier<ApiResponse> wrap(Callable<ApiResponse> attempt) {
        return () -> {
            try {
                return attempt.call();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new HttpAttemptException(ex);
            }
        };
    }

    private static final class HttpAttemptException extends RuntimeException {
        HttpAttemptException(Throwable cause) {
            super(cause);
        }
    }
}
