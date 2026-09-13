package twila.parcelemais.contracttests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

final class OpenApiSchemaFixture {

    private static final URI SWAGGER_URL =
            URI.create("https://api.staging.parcelemais.com.br/integration/swagger/v1/swagger.json");

    private static JsonNode cached;

    private OpenApiSchemaFixture() {
    }

    static synchronized JsonNode fetch() {
        if (cached != null)
            return cached;

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(SWAGGER_URL).timeout(Duration.ofSeconds(30)).GET().build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new IllegalStateException("Falha ao buscar o swagger.json de staging: HTTP " + response.statusCode());

            cached = new ObjectMapper().readTree(response.body());
            return cached;
        } catch (IOException | InterruptedException ex) {
            throw new IllegalStateException("Falha de rede ao buscar o swagger.json de staging.", ex);
        }
    }
}
