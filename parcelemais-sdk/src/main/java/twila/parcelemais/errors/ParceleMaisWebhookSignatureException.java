package twila.parcelemais.errors;

/**
 * Lançada quando a assinatura de um payload de webhook não é válida (não confere com o segredo
 * esperado, cabeçalho malformado, ou timestamp fora da janela de tolerância de replay).
 */
public final class ParceleMaisWebhookSignatureException extends ParceleMaisException {

    public ParceleMaisWebhookSignatureException(String message) {
        super(message);
    }
}
