package twila.parcelemais.errors;

import static org.assertj.core.api.Assertions.assertThat;

import twila.parcelemais.internal.http.ApiResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import org.junit.jupiter.api.Test;

class ParceleMaisExceptionFactoryTest {

    @Test
    void mapsUnauthorizedToAuthenticationException() {
        ApiResponse response = responseOf(401, "{\"detalhe\":\"credenciais inválidas\"}");

        ParceleMaisException exception = ParceleMaisExceptionFactory.fromResponse(response);

        assertThat(exception).isInstanceOf(ParceleMaisAuthenticationException.class);
        assertThat(exception.getMessage()).isEqualTo("credenciais inválidas");
    }

    @Test
    void mapsBadRequestWithFieldErrorsToValidationException() {
        ApiResponse response = responseOf(400, "{\"detalhe\":\"dados inválidos\",\"erros\":{\"cpf\":[\"obrigatório\"]}}");

        ParceleMaisException exception = ParceleMaisExceptionFactory.fromResponse(response);

        assertThat(exception).isInstanceOf(ParceleMaisValidationException.class);
        assertThat(((ParceleMaisApiException) exception).getErrors()).containsKey("cpf");
    }

    @Test
    void mapsBadRequestWithoutFieldErrorsToGenericApiException() {
        ApiResponse response = responseOf(400, "{\"detalhe\":\"requisição malformada\"}");

        ParceleMaisException exception = ParceleMaisExceptionFactory.fromResponse(response);

        assertThat(exception).isInstanceOf(ParceleMaisApiException.class).isNotInstanceOf(ParceleMaisValidationException.class);
    }

    @Test
    void mapsTooManyRequestsToRateLimitExceptionWithRetryAfter() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Retry-After", "30");
        ApiResponse response = responseOf(429, "{\"detalhe\":\"limite excedido\"}", headers);

        ParceleMaisException exception = ParceleMaisExceptionFactory.fromResponse(response);

        assertThat(exception).isInstanceOf(ParceleMaisRateLimitException.class);
        assertThat(((ParceleMaisRateLimitException) exception).getRetryAfter()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void toleratesEmptyOrInvalidBody() {
        ApiResponse response = responseOf(500, "não é json");

        ParceleMaisException exception = ParceleMaisExceptionFactory.fromResponse(response);

        assertThat(exception).isInstanceOf(ParceleMaisApiException.class);
        assertThat(((ParceleMaisApiException) exception).getStatusCode()).isEqualTo(500);
    }

    private static ApiResponse responseOf(int statusCode, String body) {
        return responseOf(statusCode, body, Collections.<String, String>emptyMap());
    }

    private static ApiResponse responseOf(int statusCode, String body, Map<String, String> headers) {
        Headers.Builder headersBuilder = new Headers.Builder();
        for (Map.Entry<String, String> entry : headers.entrySet())
            headersBuilder.add(entry.getKey(), entry.getValue());

        return new ApiResponse(statusCode, body, headersBuilder.build());
    }
}
