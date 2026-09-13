package twila.parcelemais.errors;

/**
 * Lançada quando a autenticação falha: credenciais inválidas, ou {@code 401} persistente após uma
 * tentativa de renovação de token.
 */
public final class ParceleMaisAuthenticationException extends ParceleMaisException {

    public ParceleMaisAuthenticationException(String message) {
        super(message);
    }

    public ParceleMaisAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
