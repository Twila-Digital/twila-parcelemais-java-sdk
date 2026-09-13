package twila.parcelemais;

import twila.parcelemais.config.ParceleMaisEnvironment;
import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.customers.CustomersClient;
import twila.parcelemais.errors.ParceleMaisConfigurationException;
import twila.parcelemais.orders.OrdersClient;
import twila.parcelemais.simulations.SimulationsClient;
import twila.parcelemais.webhooks.WebhooksClient;
import java.net.URI;

public interface ParceleMaisClient extends AutoCloseable {

    OrdersClient orders();

    SimulationsClient simulations();

    CustomersClient customers();

    WebhooksClient webhooks();

    @Override
    void close();

    static Builder builder() {
        return new Builder();
    }

    final class Builder {

        private String clientId;
        private String clientSecret;
        private ParceleMaisEnvironment environment = ParceleMaisEnvironment.PRODUCTION;
        private URI baseUrl;
        private ParceleMaisResilienceOptions resilience = ParceleMaisResilienceOptions.builder().build();

        private Builder() {
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder environment(ParceleMaisEnvironment environment) {
            this.environment = environment;
            return this;
        }

        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder resilience(ParceleMaisResilienceOptions resilience) {
            this.resilience = resilience;
            return this;
        }

        public ParceleMaisClient build() {
            validate();

            URI resolvedBaseUrl = resolveBaseUrl();
            return new ParceleMaisClientImpl(clientId, clientSecret, resolvedBaseUrl, resilience);
        }

        private URI resolveBaseUrl() {
            URI uri = baseUrl != null ? baseUrl : environment.toBaseUri();
            return uri.toString().endsWith("/") ? uri : URI.create(uri + "/");
        }

        private static boolean isBlank(String value) {
            return value == null || value.trim().isEmpty();
        }

        private void validate() {
            if (isBlank(clientId))
                throw new ParceleMaisConfigurationException("clientId é obrigatório.");

            if (isBlank(clientSecret))
                throw new ParceleMaisConfigurationException("clientSecret é obrigatório.");

            if (baseUrl != null && !baseUrl.isAbsolute())
                throw new ParceleMaisConfigurationException("baseUrl, quando informada, deve ser uma URI absoluta.");

            if (resilience.getMaxRetryAttempts() < 1)
                throw new ParceleMaisConfigurationException("resilience.maxRetryAttempts deve ser maior ou igual a 1.");

            if (resilience.getTotalTimeout().isZero() || resilience.getTotalTimeout().isNegative())
                throw new ParceleMaisConfigurationException("resilience.totalTimeout deve ser maior que zero.");

            if (resilience.getAttemptTimeout().isZero() || resilience.getAttemptTimeout().isNegative())
                throw new ParceleMaisConfigurationException("resilience.attemptTimeout deve ser maior que zero.");

            if (resilience.getAttemptTimeout().compareTo(resilience.getTotalTimeout()) > 0)
                throw new ParceleMaisConfigurationException("resilience.attemptTimeout não pode ser maior que resilience.totalTimeout.");

            if (resilience.getCircuitBreakerFailureRatio() <= 0 || resilience.getCircuitBreakerFailureRatio() > 1)
                throw new ParceleMaisConfigurationException("resilience.circuitBreakerFailureRatio deve estar entre 0 (exclusivo) e 1 (inclusivo).");

            if (resilience.getCircuitBreakerMinimumThroughput() < 2)
                throw new ParceleMaisConfigurationException("resilience.circuitBreakerMinimumThroughput deve ser maior ou igual a 2.");
        }
    }
}
