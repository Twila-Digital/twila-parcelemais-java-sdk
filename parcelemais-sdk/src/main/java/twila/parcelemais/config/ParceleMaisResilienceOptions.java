package twila.parcelemais.config;

import java.time.Duration;
import lombok.Builder;
import lombok.Value;

/**
 * Configuração da pipeline de resiliência (timeout, retry, circuit breaker, idempotência) do client.
 */
@Value
@Builder
public class ParceleMaisResilienceOptions {

    /**
     * Timeout total de uma chamada lógica, incluindo todas as tentativas de retry. Default: 30s.
     */
    @Builder.Default
    Duration totalTimeout = Duration.ofSeconds(30);

    /**
     * Timeout de uma única tentativa HTTP. Default: 10s.
     */
    @Builder.Default
    Duration attemptTimeout = Duration.ofSeconds(10);

    /**
     * Timeout de uma única tentativa para o endpoint de importação de nota fiscal (payload maior). Default: 60s.
     */
    @Builder.Default
    Duration invoiceUploadAttemptTimeout = Duration.ofSeconds(60);

    /**
     * Número máximo de tentativas (incluindo a original) para requisições retryable. Default: 3.
     */
    @Builder.Default
    int maxRetryAttempts = 3;

    /**
     * Delay base do backoff exponencial com jitter entre tentativas. Default: 500ms.
     */
    @Builder.Default
    Duration retryBaseDelay = Duration.ofMillis(500);

    /**
     * Proporção de falhas na janela de amostragem que abre o circuit breaker. Default: 0.5 (50%).
     */
    @Builder.Default
    double circuitBreakerFailureRatio = 0.5;

    /**
     * Janela de amostragem do circuit breaker, expressa em número de chamadas. Default: 30.
     */
    @Builder.Default
    int circuitBreakerSlidingWindowSize = 30;

    /**
     * Quantidade mínima de chamadas na janela para o circuit breaker considerar abrir. Default: 10.
     */
    @Builder.Default
    int circuitBreakerMinimumThroughput = 10;

    /**
     * Tempo que o circuit breaker permanece aberto antes de testar meia-abertura. Default: 15s.
     */
    @Builder.Default
    Duration circuitBreakerBreakDuration = Duration.ofSeconds(15);

    /**
     * Trata HTTP 500 como transitório (retryable). Default: {@code false}.
     */
    boolean retryOn500;

    /**
     * Desabilita o envio automático de {@code Idempotency-Key} em {@code POST /v1/order},
     * {@code /start-cdc-sale}, {@code /invoice} e {@code /v1/webhooks}. Esses endpoints deixam de ser
     * retryable automaticamente quando desabilitado. Default: {@code false}.
     */
    boolean disableAutomaticIdempotencyKey;
}
