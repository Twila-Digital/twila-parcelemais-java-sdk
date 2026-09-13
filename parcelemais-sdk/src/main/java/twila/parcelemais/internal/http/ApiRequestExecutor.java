package twila.parcelemais.internal.http;

import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.errors.ParceleMaisExceptionFactory;
import twila.parcelemais.internal.auth.AccessTokenProvider;
import twila.parcelemais.internal.idempotency.IdempotencyClassifier;
import twila.parcelemais.internal.resilience.ResilienceExecutor;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class ApiRequestExecutor implements AutoCloseable {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final URI baseUrl;
    private final AccessTokenProvider tokenProvider;
    private final ResilienceExecutor resilience;
    private final ParceleMaisResilienceOptions resilienceOptions;

    public ApiRequestExecutor(
            OkHttpClient httpClient,
            URI baseUrl,
            AccessTokenProvider tokenProvider,
            ResilienceExecutor resilience,
            ParceleMaisResilienceOptions resilienceOptions) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.tokenProvider = tokenProvider;
        this.resilience = resilience;
        this.resilienceOptions = resilienceOptions;
    }

    public ApiResponse get(String path) {
        return send("GET", path, null, resilienceOptions.getAttemptTimeout());
    }

    public ApiResponse post(String path, String jsonBody) {
        return send("POST", path, jsonBody, resilienceOptions.getAttemptTimeout());
    }

    public ApiResponse post(String path, String jsonBody, Duration attemptTimeout) {
        return send("POST", path, jsonBody, attemptTimeout);
    }

    public ApiResponse put(String path, String jsonBody) {
        return send("PUT", path, jsonBody, resilienceOptions.getAttemptTimeout());
    }

    public ApiResponse delete(String path) {
        return send("DELETE", path, null, resilienceOptions.getAttemptTimeout());
    }

    public static void ensureSuccess(ApiResponse response) {
        if (response.statusCode() / 100 != 2)
            throw ParceleMaisExceptionFactory.fromResponse(response);
    }

    @Override
    public void close() {
        resilience.close();
    }

    private ApiResponse send(String method, String path, String jsonBody, Duration attemptTimeout) {
        final String idempotencyKey = !resilienceOptions.isDisableAutomaticIdempotencyKey() && IdempotencyClassifier.requiresIdempotencyKey(method, path)
                ? UUID.randomUUID().toString()
                : null;

        boolean retrySafe = IdempotencyClassifier.isRetrySafe(method, path, idempotencyKey != null);

        return resilience.execute(retrySafe, () -> sendWithAuth(method, path, jsonBody, idempotencyKey, attemptTimeout));
    }

    private ApiResponse sendWithAuth(
            String method, String path, String jsonBody, String idempotencyKey, Duration attemptTimeout) throws IOException {
        String token = tokenProvider.getToken();
        ApiResponse response = sendOnce(method, path, jsonBody, idempotencyKey, attemptTimeout, token);

        if (response.statusCode() != 401)
            return response;

        tokenProvider.invalidate();
        String newToken = tokenProvider.getToken();
        ApiResponse retried = sendOnce(method, path, jsonBody, idempotencyKey, attemptTimeout, newToken);

        if (retried.statusCode() != 401)
            return retried;

        throw ParceleMaisExceptionFactory.fromResponse(retried);
    }

    private ApiResponse sendOnce(
            String method, String path, String jsonBody, String idempotencyKey, Duration attemptTimeout, String token)
            throws IOException {
        URI resolved = baseUrl.resolve(path);

        Request.Builder builder = new Request.Builder()
                .url(resolved.toString())
                .header("Authorization", "Bearer " + token);

        if (idempotencyKey != null)
            builder.header("Idempotency-Key", idempotencyKey);

        if ("GET".equals(method)) {
            builder.get();
        } else if ("DELETE".equals(method)) {
            builder.delete();
        } else if ("POST".equals(method)) {
            builder.post(RequestBody.create(jsonBody != null ? jsonBody : "", JSON));
        } else if ("PUT".equals(method)) {
            builder.put(RequestBody.create(jsonBody != null ? jsonBody : "", JSON));
        } else {
            throw new IllegalArgumentException("Método HTTP não suportado: " + method);
        }

        OkHttpClient callClient = httpClient.newBuilder()
                .callTimeout(attemptTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();

        Response response = callClient.newCall(builder.build()).execute();
        try {
            String responseBody = response.body() != null ? response.body().string() : "";
            return new ApiResponse(response.code(), responseBody, response.headers());
        } finally {
            response.close();
        }
    }
}
