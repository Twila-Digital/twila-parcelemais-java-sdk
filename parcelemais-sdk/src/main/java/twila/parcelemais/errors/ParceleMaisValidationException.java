package twila.parcelemais.errors;

/**
 * Lançada para {@code 400 Bad Request} com erros de validação de campo.
 */
public final class ParceleMaisValidationException extends ParceleMaisApiException {

    public ParceleMaisValidationException(String message, ProblemDetailsModel problemDetails) {
        super(message, 400, problemDetails);
    }
}
