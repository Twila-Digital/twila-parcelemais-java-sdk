package twila.parcelemais.errors;

/**
 * Lançada quando a configuração do client é inválida. A mensagem identifica o campo, nunca o valor.
 */
public final class ParceleMaisConfigurationException extends ParceleMaisException {

    public ParceleMaisConfigurationException(String message) {
        super(message);
    }
}
