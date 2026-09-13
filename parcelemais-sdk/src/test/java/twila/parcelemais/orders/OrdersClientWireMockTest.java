package twila.parcelemais.orders;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.orders.model.CreateOrderRequest;
import twila.parcelemais.orders.model.Order;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrdersClientWireMockTest {

    private static final String TOKEN_RESPONSE = "{\"token_de_acesso\":\"token-de-teste\",\"expira_em_segundos\":3600,\"tipo_de_token\":\"Bearer\"}";

    private WireMockServer server;
    private ParceleMaisClient client;

    @BeforeEach
    void startServer() {
        server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        configureFor("localhost", server.port());

        stubFor(post(urlEqualTo("/integration/v1/authentication/accesstoken"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(TOKEN_RESPONSE)));

        // Base URL sem "/" final de propósito — regressão do bug de resolução de URI relativa (RFC 3986 §5.3).
        client = ParceleMaisClient.builder()
                .clientId("client-id")
                .clientSecret("client-secret")
                .baseUrl(URI.create("http://localhost:" + server.port() + "/integration"))
                .resilience(ParceleMaisResilienceOptions.builder()
                        .totalTimeout(Duration.ofSeconds(5))
                        .attemptTimeout(Duration.ofSeconds(2))
                        .retryBaseDelay(Duration.ofMillis(10))
                        .build())
                .build();
    }

    @AfterEach
    void stopServer() {
        client.close();
        server.stop();
    }

    @Test
    void resolvesBaseUrlWithoutTrailingSlashCorrectly() {
        UUID orderId = UUID.randomUUID();

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/integration/v1/order/" + orderId))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(orderJson(orderId))));

        Order order = client.orders().get(orderId);

        assertThat(order.getId()).isEqualTo(orderId);
        server.verify(getRequestedFor(urlEqualTo("/integration/v1/order/" + orderId)));
    }

    @Test
    void retriesOnTransientFailureThenSucceeds() {
        UUID orderId = UUID.randomUUID();
        String url = "/integration/v1/order/" + orderId;

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo(url))
                .inScenario("retry")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("retried"));

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo(url))
                .inScenario("retry")
                .whenScenarioStateIs("retried")
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(orderJson(orderId))));

        Order order = client.orders().get(orderId);

        assertThat(order.getId()).isEqualTo(orderId);
        server.verify(2, getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void refreshesTokenOnceOn401ThenSucceeds() {
        UUID orderId = UUID.randomUUID();
        String url = "/integration/v1/order/" + orderId;

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo(url))
                .inScenario("auth-retry")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(401))
                .willSetStateTo("token-renovado"));

        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo(url))
                .inScenario("auth-retry")
                .whenScenarioStateIs("token-renovado")
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(orderJson(orderId))));

        Order order = client.orders().get(orderId);

        assertThat(order.getId()).isEqualTo(orderId);
        server.verify(2, getRequestedFor(urlEqualTo(url)));
        server.verify(2, postRequestedFor(urlEqualTo("/integration/v1/authentication/accesstoken")));
    }

    @Test
    void sendsTheSameIdempotencyKeyAcrossRetriesOnCreate() {
        stubFor(post(urlEqualTo("/integration/v1/order"))
                .inScenario("idempotency")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(503))
                .willSetStateTo("retried"));

        UUID orderId = UUID.randomUUID();
        stubFor(post(urlEqualTo("/integration/v1/order"))
                .inScenario("idempotency")
                .whenScenarioStateIs("retried")
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"pedidoId\":\"" + orderId + "\"}")));

        CreateOrderRequest request = CreateOrderRequest.builder()
                .cpf("12345678901")
                .phoneNumber("+5511999998888")
                .establishmentDocument("12345678000195")
                .requestedAmount(new BigDecimal("1500.00"))
                .name("Maria Souza")
                .email("maria@exemplo.com.br")
                .dateOfBirth(OffsetDateTime.now().minusYears(30))
                .address(twila.parcelemais.orders.model.Address.builder()
                        .street("Av. Paulista").number("1578").neighborhood("Bela Vista")
                        .city("São Paulo").state("SP").postalCode("01311000").build())
                .build();

        UUID createdId = client.orders().create(request);

        assertThat(createdId).isEqualTo(orderId);

        List<LoggedRequest> requests = server.findAll(postRequestedFor(urlEqualTo("/integration/v1/order")));
        assertThat(requests).hasSize(2);

        String firstKey = requests.get(0).getHeader("Idempotency-Key");
        String secondKey = requests.get(1).getHeader("Idempotency-Key");

        assertThat(firstKey).isNotBlank();
        assertThat(firstKey).isEqualTo(secondKey);
    }

    private static String orderJson(UUID orderId) {
        return "{"
                + "\"id\":\"" + orderId + "\","
                + "\"numero\":123,"
                + "\"status\":{\"valor\":9,\"descricao\":\"Comprado\"},"
                + "\"documentoCliente\":\"12345678901\","
                + "\"razaoSocialEstabelecimento\":\"Loja Exemplo\","
                + "\"documentoEstabelecimento\":\"12345678000195\","
                + "\"criadoEm\":\"2026-01-01T10:00:00-03:00\""
                + "}";
    }
}
