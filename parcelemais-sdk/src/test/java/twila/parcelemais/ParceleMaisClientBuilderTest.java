package twila.parcelemais;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.errors.ParceleMaisConfigurationException;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ParceleMaisClientBuilderTest {

    @Test
    void requiresClientId() {
        assertThatThrownBy(() -> ParceleMaisClient.builder().clientSecret("secret").build())
                .isInstanceOf(ParceleMaisConfigurationException.class)
                .hasMessageContaining("clientId");
    }

    @Test
    void requiresClientSecret() {
        assertThatThrownBy(() -> ParceleMaisClient.builder().clientId("id").build())
                .isInstanceOf(ParceleMaisConfigurationException.class)
                .hasMessageContaining("clientSecret");
    }

    @Test
    void rejectsRelativeBaseUrl() {
        assertThatThrownBy(() -> ParceleMaisClient.builder()
                        .clientId("id")
                        .clientSecret("secret")
                        .baseUrl(URI.create("/relative"))
                        .build())
                .isInstanceOf(ParceleMaisConfigurationException.class)
                .hasMessageContaining("baseUrl");
    }

    @Test
    void rejectsAttemptTimeoutGreaterThanTotalTimeout() {
        ParceleMaisResilienceOptions resilience = ParceleMaisResilienceOptions.builder()
                .totalTimeout(Duration.ofSeconds(5))
                .attemptTimeout(Duration.ofSeconds(10))
                .build();

        assertThatThrownBy(() -> ParceleMaisClient.builder()
                        .clientId("id")
                        .clientSecret("secret")
                        .resilience(resilience)
                        .build())
                .isInstanceOf(ParceleMaisConfigurationException.class)
                .hasMessageContaining("attemptTimeout");
    }

    @Test
    void rejectsMaxRetryAttemptsBelowOne() {
        ParceleMaisResilienceOptions resilience = ParceleMaisResilienceOptions.builder().maxRetryAttempts(0).build();

        assertThatThrownBy(() -> ParceleMaisClient.builder()
                        .clientId("id")
                        .clientSecret("secret")
                        .resilience(resilience)
                        .build())
                .isInstanceOf(ParceleMaisConfigurationException.class)
                .hasMessageContaining("maxRetryAttempts");
    }

    @Test
    void buildsSuccessfullyWithValidConfiguration() {
        try (ParceleMaisClient client = ParceleMaisClient.builder()
                .clientId("id")
                .clientSecret("secret")
                .build()) {
            org.assertj.core.api.Assertions.assertThat(client.orders()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(client.simulations()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(client.customers()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(client.webhooks()).isNotNull();
        }
    }
}
