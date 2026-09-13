package twila.parcelemais.contracttests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WireContractTests {

    @ParameterizedTest
    @ValueSource(strings = {
            "/v1/authentication/accesstoken",
            "/v1/order",
            "/v1/order/{id}",
            "/v1/order/paged",
            "/v1/order/start-cdc-sale",
            "/v1/order/invoice",
            "/v1/order/simulate-installments",
            "/v1/order/simulate-values",
            "/v1/customer/{id}",
            "/v1/customer/paged",
            "/v1/webhooks",
            "/v1/webhooks/{type}"
    })
    void endpointExistsInStagingSchema(String path) {
        var paths = OpenApiSchemaFixture.fetch().path("paths");

        var matches = paths.properties().stream().anyMatch(entry -> entry.getKey().endsWith(normalize(path)));

        assertThat(matches)
                .withFailMessage("Endpoint '%s' não encontrado no swagger.json de staging — o SDK e o backend divergiram.", path)
                .isTrue();
    }

    @Test
    void orderResponseHasTheFieldsTheWireDtoExpects() {
        var schemas = OpenApiSchemaFixture.fetch().path("components").path("schemas");

        var orderSchema = schemas.properties().stream()
                .filter(entry -> entry.getKey().toLowerCase().contains("orderresponse") || entry.getKey().toLowerCase().contains("pedidoresponse"))
                .map(java.util.Map.Entry::getValue)
                .findFirst();

        assertThat(orderSchema)
                .withFailMessage("Não encontrei o schema de resposta de pedido no swagger.json de staging.")
                .isPresent();

        var properties = orderSchema.get().path("properties");

        for (var expectedField : new String[] {"id", "numero", "status", "documentoCliente", "criadoEm"}) {
            assertThat(properties.has(expectedField))
                    .withFailMessage("Campo '%s' esperado pelo OrderWire não existe (mais) no schema de staging.", expectedField)
                    .isTrue();
        }
    }

    private static String normalize(String templatePath) {
        return templatePath.replaceAll("\\{[^}]+}", "");
    }
}
