package twila.parcelemais.establishments;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.errors.ParceleMaisApiException;
import twila.parcelemais.establishments.model.BankAccountType;
import twila.parcelemais.establishments.model.CreateEstablishmentRequest;
import twila.parcelemais.establishments.model.DisbursementModel;
import twila.parcelemais.establishments.model.Establishment;
import twila.parcelemais.establishments.model.EstablishmentAddress;
import twila.parcelemais.establishments.model.EstablishmentBankAccount;
import twila.parcelemais.establishments.model.EstablishmentOwner;
import twila.parcelemais.establishments.model.ListEstablishmentsRequest;
import twila.parcelemais.establishments.model.UpdateEstablishmentRequest;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EstablishmentsClientWireMockTest {

    private static final String TOKEN_RESPONSE =
            "{\"token_de_acesso\":\"token-de-teste\",\"expira_em_segundos\":3600,\"tipo_de_token\":\"Bearer\"}";

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    private static String establishmentJson(UUID establishmentId) {
        return "{"
                + "\"estabelecimentoId\":\"" + establishmentId + "\","
                + "\"documento\":\"12345678000199\","
                + "\"razaoSocial\":\"Loja Centro LTDA\","
                + "\"nomeFantasia\":\"Loja Centro\","
                + "\"ativa\":true,"
                + "\"modeloDesembolso\":1,"
                + "\"responsavel\":{\"nome\":\"Maria Souza\",\"email\":\"maria@loja.com.br\",\"celular\":\"+5511999998888\"},"
                + "\"contaBancaria\":{\"banco\":\"341\",\"agencia\":\"1234\",\"digitoAgencia\":\"\",\"conta\":\"56789\","
                + "\"digitoConta\":\"0\",\"tipoConta\":1,\"nomeTitular\":null,\"documentoTitular\":null},"
                + "\"endereco\":{\"rua\":\"Rua Exemplo\",\"numero\":\"100\",\"complemento\":null,\"bairro\":\"Centro\","
                + "\"cidade\":\"Sao Paulo\",\"estado\":\"SP\",\"cep\":\"01310100\",\"pais\":\"Brasil\"}"
                + "}";
    }

    private static EstablishmentAddress address() {
        return EstablishmentAddress.builder()
                .street("Rua Exemplo")
                .number("100")
                .district("Centro")
                .city("Sao Paulo")
                .state("SP")
                .zipCode("01310100")
                .build();
    }

    private static CreateEstablishmentRequest createRequest(EstablishmentAddress address) {
        return CreateEstablishmentRequest.builder()
                .document("12345678000199")
                .legalName("Loja Centro LTDA")
                .tradeName("Loja Centro")
                .disbursementModel(DisbursementModel.ESTABLISHMENT_CHAIN)
                .owner(EstablishmentOwner.builder()
                        .name("Maria Souza")
                        .email("maria@loja.com.br")
                        .phone("+5511999998888")
                        .build())
                .bankAccount(EstablishmentBankAccount.builder()
                        .bankNumber("341")
                        .agencyNumber("1234")
                        .accountNumber("56789")
                        .accountDigit("0")
                        .accountType(BankAccountType.CURRENT)
                        .build())
                .address(address)
                .build();
    }

    private JsonNode lastBody(List<LoggedRequest> requests) throws Exception {
        assertThat(requests).isNotEmpty();

        return MAPPER.readTree(requests.get(requests.size() - 1).getBodyAsString());
    }

    @Test
    void createSendsWireBodyAndReturnsId() throws Exception {
        UUID establishmentId = UUID.randomUUID();

        stubFor(post(urlEqualTo("/integration/v1/establishment"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"estabelecimentoId\":\"" + establishmentId + "\"}")));

        UUID created = client.establishments().create(createRequest(address()));

        assertThat(created).isEqualTo(establishmentId);

        JsonNode body = lastBody(server.findAll(postRequestedFor(urlEqualTo("/integration/v1/establishment"))));
        assertThat(body.get("documento").asText()).isEqualTo("12345678000199");
        assertThat(body.get("razaoSocial").asText()).isEqualTo("Loja Centro LTDA");
        assertThat(body.get("modeloDesembolso").asInt()).isEqualTo(1);
        assertThat(body.get("responsavel").get("celular").asText()).isEqualTo("+5511999998888");
        assertThat(body.get("contaBancaria").get("tipoConta").asInt()).isEqualTo(1);
        assertThat(body.get("endereco").get("rua").asText()).isEqualTo("Rua Exemplo");
        assertThat(body.get("endereco").get("numero").asText()).isEqualTo("100");
        assertThat(body.get("endereco").get("bairro").asText()).isEqualTo("Centro");
        assertThat(body.get("endereco").get("cidade").asText()).isEqualTo("Sao Paulo");
        assertThat(body.get("endereco").get("estado").asText()).isEqualTo("SP");
        assertThat(body.get("endereco").get("cep").asText()).isEqualTo("01310100");
    }

    @Test
    void createRequestWithoutAddressFailsOnBuild() {
        assertThatThrownBy(() -> createRequest(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("address");
    }

    @Test
    void getMapsEstablishment() {
        UUID establishmentId = UUID.randomUUID();
        String url = "/integration/v1/establishment/" + establishmentId;

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody(establishmentJson(establishmentId))));

        Establishment establishment = client.establishments().get(establishmentId);

        assertThat(establishment.getEstablishmentId()).isEqualTo(establishmentId);
        assertThat(establishment.getTradeName()).isEqualTo("Loja Centro");
        assertThat(establishment.isActive()).isTrue();
        assertThat(establishment.getDisbursementModel()).isEqualTo(DisbursementModel.ESTABLISHMENT_CHAIN);
        assertThat(establishment.getOwner().getPhone()).isEqualTo("+5511999998888");
        assertThat(establishment.getBankAccount().getAccountType()).isEqualTo(BankAccountType.CURRENT);
        assertThat(establishment.getAddress().getCity()).isEqualTo("Sao Paulo");
        server.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void getWithoutBankAccountAndAddress() {
        UUID establishmentId = UUID.randomUUID();
        String json = "{"
                + "\"estabelecimentoId\":\"" + establishmentId + "\","
                + "\"documento\":\"12345678000199\","
                + "\"razaoSocial\":\"Loja Centro LTDA\","
                + "\"nomeFantasia\":\"Loja Centro\","
                + "\"ativa\":false,"
                + "\"modeloDesembolso\":null,"
                + "\"responsavel\":{\"nome\":\"Maria Souza\",\"email\":\"maria@loja.com.br\",\"celular\":\"+5511999998888\"},"
                + "\"contaBancaria\":null,"
                + "\"endereco\":null"
                + "}";

        stubFor(get(urlEqualTo("/integration/v1/establishment/" + establishmentId))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(json)));

        Establishment establishment = client.establishments().get(establishmentId);

        assertThat(establishment.isActive()).isFalse();
        assertThat(establishment.getDisbursementModel()).isNull();
        assertThat(establishment.getBankAccount()).isNull();
        assertThat(establishment.getAddress()).isNull();
    }

    @Test
    void listBuildsQueryString() {
        UUID establishmentId = UUID.randomUUID();
        String url = "/integration/v1/establishment/list?nomeFantasia=Centro&ativa=true";

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("[" + establishmentJson(establishmentId) + "]")));

        List<Establishment> establishments = client.establishments()
                .list(ListEstablishmentsRequest.builder().tradeName("Centro").isActive(true).build());

        assertThat(establishments).hasSize(1);
        assertThat(establishments.get(0).getTradeName()).isEqualTo("Loja Centro");
        server.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void listWithoutFiltersSendsNoQueryString() {
        String url = "/integration/v1/establishment/list";

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[]")));

        List<Establishment> establishments = client.establishments()
                .list(ListEstablishmentsRequest.builder().build());

        assertThat(establishments).isEmpty();
        server.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void listInactiveSendsFalse() {
        String url = "/integration/v1/establishment/list?ativa=false";

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("[]")));

        List<Establishment> establishments = client.establishments()
                .list(ListEstablishmentsRequest.builder().isActive(false).build());

        assertThat(establishments).isEmpty();
        server.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    void updateSendsOnlyEditableFields() throws Exception {
        UUID establishmentId = UUID.randomUUID();
        String url = "/integration/v1/establishment/" + establishmentId;

        stubFor(put(urlEqualTo(url)).willReturn(aResponse().withStatus(200)));

        client.establishments().update(establishmentId,
                UpdateEstablishmentRequest.builder().tradeName("Loja Centro Matriz").build());

        JsonNode body = lastBody(server.findAll(putRequestedFor(urlEqualTo(url))));
        assertThat(body.get("nomeFantasia").asText()).isEqualTo("Loja Centro Matriz");
        assertThat(body.has("contaBancaria")).isFalse();
    }

    @Test
    void updateBankAccountUsesOwnEndpoint() throws Exception {
        UUID establishmentId = UUID.randomUUID();
        String url = "/integration/v1/establishment/" + establishmentId + "/bank-account";

        stubFor(put(urlEqualTo(url)).willReturn(aResponse().withStatus(200)));

        client.establishments().updateBankAccount(establishmentId, EstablishmentBankAccount.builder()
                .bankNumber("237")
                .agencyNumber("4321")
                .accountNumber("98765")
                .accountDigit("1")
                .accountType(BankAccountType.SAVINGS)
                .build());

        JsonNode body = lastBody(server.findAll(putRequestedFor(urlEqualTo(url))));
        assertThat(body.get("banco").asText()).isEqualTo("237");
        assertThat(body.get("tipoConta").asInt()).isEqualTo(2);
    }

    @Test
    void activateAndDeactivateSendStatus() throws Exception {
        UUID establishmentId = UUID.randomUUID();
        String url = "/integration/v1/establishment/" + establishmentId + "/status";

        stubFor(put(urlEqualTo(url)).willReturn(aResponse().withStatus(200)));

        client.establishments().deactivate(establishmentId);
        client.establishments().activate(establishmentId);

        List<LoggedRequest> requests = server.findAll(putRequestedFor(urlEqualTo(url)));
        assertThat(requests).hasSize(2);
        assertThat(MAPPER.readTree(requests.get(0).getBodyAsString()).get("ativa").asBoolean()).isFalse();
        assertThat(MAPPER.readTree(requests.get(1).getBodyAsString()).get("ativa").asBoolean()).isTrue();
    }

    @Test
    void createConflictThrowsApiException() {
        stubFor(post(urlEqualTo("/integration/v1/establishment"))
                .willReturn(aResponse().withStatus(409).withHeader("Content-Type", "application/json")
                        .withBody("{\"tipo\":\"Establishment.DocumentAlreadyAdded\",\"detalhe\":\"Documento ja cadastrado.\"}")));

        assertThatThrownBy(() -> client.establishments().create(createRequest(address())))
                .isInstanceOf(ParceleMaisApiException.class)
                .satisfies(thrown -> assertThat(((ParceleMaisApiException) thrown).getStatusCode()).isEqualTo(409));
    }

    @Test
    void getNotFoundThrowsApiException() {
        UUID establishmentId = UUID.randomUUID();

        stubFor(get(urlEqualTo("/integration/v1/establishment/" + establishmentId))
                .willReturn(aResponse().withStatus(404).withHeader("Content-Type", "application/json")
                        .withBody("{\"tipo\":\"Establishment.EstablishmentNotFound\",\"detalhe\":\"Estabelecimento nao encontrado.\"}")));

        assertThatThrownBy(() -> client.establishments().get(establishmentId))
                .isInstanceOf(ParceleMaisApiException.class)
                .satisfies(thrown -> assertThat(((ParceleMaisApiException) thrown).getStatusCode()).isEqualTo(404));
    }
}
