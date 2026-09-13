package twila.parcelemais;

import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.customers.CustomersClient;
import twila.parcelemais.customers.CustomersClientImpl;
import twila.parcelemais.internal.auth.AccessTokenProvider;
import twila.parcelemais.internal.auth.AccessTokenProviderImpl;
import twila.parcelemais.internal.auth.TokenApiClientImpl;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.resilience.ResilienceExecutor;
import twila.parcelemais.orders.OrdersClient;
import twila.parcelemais.orders.OrdersClientImpl;
import twila.parcelemais.simulations.SimulationsClient;
import twila.parcelemais.simulations.SimulationsClientImpl;
import twila.parcelemais.webhooks.WebhooksClient;
import twila.parcelemais.webhooks.WebhooksClientImpl;
import java.net.URI;
import okhttp3.OkHttpClient;

final class ParceleMaisClientImpl implements ParceleMaisClient {

    private final OkHttpClient httpClient;
    private final ResilienceExecutor resilienceExecutor;
    private final ApiRequestExecutor apiRequestExecutor;

    private final OrdersClient orders;
    private final SimulationsClient simulations;
    private final CustomersClient customers;
    private final WebhooksClient webhooks;

    ParceleMaisClientImpl(String clientId, String clientSecret, URI baseUrl, ParceleMaisResilienceOptions resilience) {
        this.httpClient = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();

        TokenApiClientImpl tokenApiClient = new TokenApiClientImpl(httpClient, baseUrl, resilience.getAttemptTimeout());
        AccessTokenProvider tokenProvider = new AccessTokenProviderImpl(tokenApiClient, clientId, clientSecret);

        this.resilienceExecutor = new ResilienceExecutor(resilience);
        this.apiRequestExecutor = new ApiRequestExecutor(httpClient, baseUrl, tokenProvider, resilienceExecutor, resilience);

        this.orders = new OrdersClientImpl(apiRequestExecutor, resilience);
        this.simulations = new SimulationsClientImpl(apiRequestExecutor);
        this.customers = new CustomersClientImpl(apiRequestExecutor);
        this.webhooks = new WebhooksClientImpl(apiRequestExecutor);
    }

    @Override
    public OrdersClient orders() {
        return orders;
    }

    @Override
    public SimulationsClient simulations() {
        return simulations;
    }

    @Override
    public CustomersClient customers() {
        return customers;
    }

    @Override
    public WebhooksClient webhooks() {
        return webhooks;
    }

    @Override
    public void close() {
        apiRequestExecutor.close();
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
