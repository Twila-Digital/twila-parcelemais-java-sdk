package twila.parcelemais.errors;

/**
 * Lançada em timeout (por tentativa ou total) ou quando o circuit breaker está aberto.
 */
public final class ParceleMaisTimeoutException extends ParceleMaisException {

    public ParceleMaisTimeoutException(String message) {
        super(message);
    }

    public ParceleMaisTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
