package twila.parcelemais.internal.auth;

import twila.parcelemais.errors.ParceleMaisAuthenticationException;
import twila.parcelemais.errors.ParceleMaisExceptionFactory;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class TokenApiClientImpl implements TokenApiClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final URI tokenEndpoint;
    private final Duration timeout;

    public TokenApiClientImpl(OkHttpClient httpClient, URI baseUrl, Duration timeout) {
        this.httpClient = httpClient;
        this.tokenEndpoint = baseUrl.resolve("v1/authentication/accesstoken");
        this.timeout = timeout;
    }

    @Override
    public GenerateAccessTokenResponse generate(String clientId, String clientSecret) {
        try {
            String body = ParceleMaisObjectMapper.DEFAULT.writeValueAsString(new GenerateAccessTokenRequest(clientId, clientSecret));

            Request request = new Request.Builder()
                    .url(tokenEndpoint.toString())
                    .post(RequestBody.create(body, JSON))
                    .build();

            OkHttpClient callClient = httpClient.newBuilder()
                    .callTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    .build();

            ApiResponse response;
            Response rawResponse = callClient.newCall(request).execute();
            try {
                String responseBody = rawResponse.body() != null ? rawResponse.body().string() : "";
                response = new ApiResponse(rawResponse.code(), responseBody, rawResponse.headers());
            } finally {
                rawResponse.close();
            }

            if (response.statusCode() / 100 != 2)
                throw ParceleMaisExceptionFactory.fromResponse(response);

            GenerateAccessTokenResponse result = ParceleMaisObjectMapper.DEFAULT.readValue(response.body(), GenerateAccessTokenResponse.class);

            if (result == null)
                throw new ParceleMaisAuthenticationException("A API do Parcele+ retornou uma resposta vazia ao gerar o token de acesso.");

            return result;
        } catch (IOException ex) {
            throw new ParceleMaisAuthenticationException("Falha de rede ao gerar o token de acesso.", ex);
        }
    }
}
