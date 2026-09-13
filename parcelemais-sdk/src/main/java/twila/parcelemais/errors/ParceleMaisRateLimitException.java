package twila.parcelemais.errors;

import java.time.Duration;

/**
 * Lançada para {@code 429 Too Many Requests}.
 */
public final class ParceleMaisRateLimitException extends ParceleMaisApiException {

    private final Duration retryAfter;

    public ParceleMaisRateLimitException(String message, ProblemDetailsModel problemDetails, Duration retryAfter) {
        super(message, 429, problemDetails);
        this.retryAfter = retryAfter;
    }

    /**
     * Cabeçalho {@code Retry-After} da resposta, quando presente.
     */
    public Duration getRetryAfter() {
        return retryAfter;
    }
}
