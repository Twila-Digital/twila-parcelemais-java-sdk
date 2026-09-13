package twila.parcelemais.errors;

/**
 * Exceção base para todos os erros lançados pelo SDK do Parcele+.
 */
public abstract class ParceleMaisException extends RuntimeException {

    protected ParceleMaisException(String message) {
        super(message);
    }

    protected ParceleMaisException(String message, Throwable cause) {
        super(message, cause);
    }
}
