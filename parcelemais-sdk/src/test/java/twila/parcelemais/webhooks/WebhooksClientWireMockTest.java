package twila.parcelemais.webhooks;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import twila.parcelemais.PagedResult;
import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.errors.ParceleMaisApiException;
import twila.parcelemais.webhooks.model.ListWebhookAuditRequest;
import twila.parcelemais.webhooks.model.WebHookType;
import twila.parcelemais.webhooks.model.WebhookAudit;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebhooksClientWireMockTest {

    private static final String TOKEN_RESPONSE =
            "{\"token_de_acesso\":\"token-de-teste\",\"expira_em_segundos\":3600,\"tipo_de_token\":\"Bearer\"}";

    private static final String AUDIT_PATH = "/integration/v1/webhooks/auditoria";

    private WireMockServer server;
    private ParceleMaisClient client;

    @BeforeEach
    void startServer() {
        server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        configureFor("localhost", server.port());

        stubFor(post(urlEqualTo("/integration/v1/authentication/accesstoken"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(TOKEN_RESPONSE)));

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
    void listAuditWithoutFiltersSendsOnlyPagingDefaults() {
        String url = AUDIT_PATH + "?pagina=1&tamanhoPagina=10";

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(pagedJson("", false, false, 1, 10, 0))));

        PagedResult<WebhookAudit> page = client.webhooks().listAudit();

        assertThat(page.getItems()).isEmpty();
        assertThat(page.getTotalCount()).isZero();
        server.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void listAuditSendsAllFiltersInQueryString() {
        UUID orderId = UUID.randomUUID();

        stubFor(get(urlPathEqualTo(AUDIT_PATH))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(pagedJson("", false, true, 2, 20, 20))));

        client.webhooks().listAudit(ListWebhookAuditRequest.builder()
                .startDate(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(-3)))
                .endDate(OffsetDateTime.of(2026, 1, 31, 23, 59, 59, 0, ZoneOffset.ofHours(-3)))
                .orderId(orderId)
                .orderNumber(123L)
                .statusCode(500)
                .page(2)
                .pageSize(20)
                .build());

        server.verify(getRequestedFor(urlPathEqualTo(AUDIT_PATH))
                .withQueryParam("dataInicio", equalTo("2026-01-01T00:00:00-03:00"))
                .withQueryParam("dataFim", equalTo("2026-01-31T23:59:59-03:00"))
                .withQueryParam("pedidoId", equalTo(orderId.toString()))
                .withQueryParam("numeroPedido", equalTo("123"))
                .withQueryParam("statusCode", equalTo("500"))
                .withQueryParam("pagina", equalTo("2"))
                .withQueryParam("tamanhoPagina", equalTo("20")));

        List<LoggedRequest> requests = server.findAll(getRequestedFor(urlPathEqualTo(AUDIT_PATH)));
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getQueryParams()).hasSize(7);
    }

    @Test
    void listAuditOmitsNullFilters() {
        String url = AUDIT_PATH + "?statusCode=404&pagina=1&tamanhoPagina=10";

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(pagedJson("", false, false, 1, 10, 0))));

        client.webhooks().listAudit(ListWebhookAuditRequest.builder().statusCode(404).build());

        server.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void listAuditMapsItemsAndPaging() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        String items = auditJson(firstId, 3, 500, "2026-01-02T10:00:00-03:00")
                + "," + auditJson(secondId, 1, 200, "2026-01-01T09:30:00-03:00");

        stubFor(get(urlPathEqualTo(AUDIT_PATH))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(pagedJson(items, true, false, 1, 2, 5))));

        PagedResult<WebhookAudit> page = client.webhooks()
                .listAudit(ListWebhookAuditRequest.builder().pageSize(2).build());

        assertThat(page.getItems()).hasSize(2);
        assertThat(page.isHasNext()).isTrue();
        assertThat(page.isHasPrevious()).isFalse();
        assertThat(page.getPageNumber()).isEqualTo(1);
        assertThat(page.getPageSize()).isEqualTo(2);
        assertThat(page.getTotalCount()).isEqualTo(5);

        WebhookAudit first = page.getItems().get(0);
        assertThat(first.getId()).isEqualTo(firstId);
        assertThat(first.getType()).isEqualTo(WebHookType.ORDER);
        assertThat(first.getRequest()).isEqualTo("{\"pedidoId\":\"abc\"}");
        assertThat(first.getResponse()).isEqualTo("erro interno");
        assertThat(first.getStatusCode()).isEqualTo(500);
        assertThat(first.getCreatedAt()).isEqualTo(OffsetDateTime.of(2026, 1, 2, 10, 0, 0, 0, ZoneOffset.ofHours(-3)));

        WebhookAudit second = page.getItems().get(1);
        assertThat(second.getId()).isEqualTo(secondId);
        assertThat(second.getType()).isEqualTo(WebHookType.CUSTOMER);
        assertThat(second.getStatusCode()).isEqualTo(200);
    }

    @Test
    void listAuditErrorThrowsApiException() {
        stubFor(get(urlPathEqualTo(AUDIT_PATH))
                .willReturn(aResponse().withStatus(404).withHeader("Content-Type", "application/json")
                        .withBody("{\"tipo\":\"WebHook.NotFound\",\"detalhe\":\"Nao encontrado.\"}")));

        assertThatThrownBy(() -> client.webhooks().listAudit())
                .isInstanceOf(ParceleMaisApiException.class)
                .satisfies(thrown -> assertThat(((ParceleMaisApiException) thrown).getStatusCode()).isEqualTo(404));
    }

    @Test
    void listAuditMalformedResponseThrowsIllegalStateException() {
        stubFor(get(urlPathEqualTo(AUDIT_PATH))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{isto não é json")));

        assertThatThrownBy(() -> client.webhooks().listAudit())
                .isInstanceOf(IllegalStateException.class);
    }

    private static String auditJson(UUID id, int type, int statusCode, String createdAt) {
        return "{"
                + "\"id\":\"" + id + "\","
                + "\"tipo\":" + type + ","
                + "\"requisicao\":\"{\\\"pedidoId\\\":\\\"abc\\\"}\","
                + "\"resposta\":\"erro interno\","
                + "\"statusCode\":" + statusCode + ","
                + "\"dataCriacao\":\"" + createdAt + "\""
                + "}";
    }

    private static String pagedJson(String items, boolean hasNext, boolean hasPrevious, int number, int size, int total) {
        return "{"
                + "\"itens\":[" + items + "],"
                + "\"pagina\":{\"tem_proximo\":" + hasNext + ",\"tem_anterior\":" + hasPrevious
                + ",\"numero\":" + number + ",\"tamanho\":" + size + ",\"total\":" + total + "}"
                + "}";
    }
}
