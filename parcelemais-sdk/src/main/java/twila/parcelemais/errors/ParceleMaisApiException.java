package twila.parcelemais.errors;

import java.util.Map;

/**
 * Lançada para qualquer resposta de erro HTTP de negócio (400/404/409/422/5xx) da API do Parcele+.
 */
public class ParceleMaisApiException extends ParceleMaisException {

    private final int statusCode;
    private final ProblemDetailsModel problemDetails;

    public ParceleMaisApiException(String message, int statusCode, ProblemDetailsModel problemDetails) {
        super(message);
        this.statusCode = statusCode;
        this.problemDetails = problemDetails;
    }

    /**
     * Código HTTP da resposta.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Campo {@code tipo} do ProblemDetails: string opaca, não um enum fechado.
     */
    public String getErrorCode() {
        return problemDetails.getType();
    }

    /**
     * Erros por campo. {@code null} quando a API não os retornou para este erro.
     */
    public Map<String, String[]> getErrors() {
        return problemDetails.getErrors();
    }

    /**
     * Campo {@code correlationId} da resposta, quando presente.
     */
    public String getCorrelationId() {
        return problemDetails.getCorrelationId();
    }

    /**
     * O {@code ProblemDetails} bruto.
     */
    public ProblemDetailsModel getProblemDetails() {
        return problemDetails;
    }
}
