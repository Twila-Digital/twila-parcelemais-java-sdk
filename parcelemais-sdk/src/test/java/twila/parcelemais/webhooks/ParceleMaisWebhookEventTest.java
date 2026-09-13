package twila.parcelemais.webhooks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import twila.parcelemais.errors.ParceleMaisWebhookSignatureException;
import twila.parcelemais.orders.model.OrderStatus;
import twila.parcelemais.webhooks.model.OrderWebhookEvent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ParceleMaisWebhookEventTest {

    private static final String SIGNING_SECRET = "segredo-de-teste";
    private static final String SIXTY_FOUR_ZEROS = "0000000000000000000000000000000000000000000000000000000000000000";

    @Test
    void parsesWithoutSignatureVerification() {
        UUID orderId = UUID.randomUUID();
        String rawJson = "{\"id_pedido\":\"" + orderId + "\",\"enum_status\":9,\"status\":\"Comprado\"}";

        OrderWebhookEvent event = ParceleMaisWebhookEvent.parse(rawJson);

        assertThat(event.getOrderId()).isEqualTo(orderId);
        assertThat(event.getStatus()).isEqualTo(OrderStatus.PURCHASED);
        assertThat(event.getStatusRaw()).isEqualTo(9);
        assertThat(event.getStatusName()).isEqualTo("Comprado");
    }

    @Test
    void unrecognizedStatusFallsBackToUnknown() {
        String rawJson = "{\"id_pedido\":\"" + UUID.randomUUID() + "\",\"enum_status\":9999,\"status\":\"Algo novo\"}";

        OrderWebhookEvent event = ParceleMaisWebhookEvent.parse(rawJson);

        assertThat(event.getStatus()).isEqualTo(OrderStatus.UNKNOWN);
        assertThat(event.getStatusRaw()).isEqualTo(9999);
    }

    @Test
    void acceptsAValidSignature() {
        String rawJson = "{\"id_pedido\":\"" + UUID.randomUUID() + "\",\"enum_status\":9,\"status\":\"Comprado\"}";
        long timestamp = Instant.now().getEpochSecond();
        String signature = ParceleMaisWebhookEvent.computeSignature(SIGNING_SECRET, timestamp, rawJson);
        String header = "t=" + timestamp + ",v1=" + signature;

        OrderWebhookEvent event = ParceleMaisWebhookEvent.parse(rawJson, header, SIGNING_SECRET);

        assertThat(event.getStatus()).isEqualTo(OrderStatus.PURCHASED);
    }

    @Test
    void rejectsAnInvalidSignature() {
        String rawJson = "{\"id_pedido\":\"" + UUID.randomUUID() + "\",\"enum_status\":9,\"status\":\"Comprado\"}";
        long timestamp = Instant.now().getEpochSecond();
        String header = "t=" + timestamp + ",v1=" + SIXTY_FOUR_ZEROS;

        assertThatThrownBy(() -> ParceleMaisWebhookEvent.parse(rawJson, header, SIGNING_SECRET))
                .isInstanceOf(ParceleMaisWebhookSignatureException.class);
    }

    @Test
    void rejectsATamperedPayload() {
        String rawJson = "{\"id_pedido\":\"" + UUID.randomUUID() + "\",\"enum_status\":9,\"status\":\"Comprado\"}";
        long timestamp = Instant.now().getEpochSecond();
        String signature = ParceleMaisWebhookEvent.computeSignature(SIGNING_SECRET, timestamp, rawJson);
        String header = "t=" + timestamp + ",v1=" + signature;

        String tamperedJson = rawJson.replace("Comprado", "Cancelado");

        assertThatThrownBy(() -> ParceleMaisWebhookEvent.parse(tamperedJson, header, SIGNING_SECRET))
                .isInstanceOf(ParceleMaisWebhookSignatureException.class);
    }

    @Test
    void rejectsAnExpiredTimestamp() {
        String rawJson = "{\"id_pedido\":\"" + UUID.randomUUID() + "\",\"enum_status\":9,\"status\":\"Comprado\"}";
        long oldTimestamp = Instant.now().minusSeconds(600).getEpochSecond();
        String signature = ParceleMaisWebhookEvent.computeSignature(SIGNING_SECRET, oldTimestamp, rawJson);
        String header = "t=" + oldTimestamp + ",v1=" + signature;

        assertThatThrownBy(() -> ParceleMaisWebhookEvent.parse(rawJson, header, SIGNING_SECRET))
                .isInstanceOf(ParceleMaisWebhookSignatureException.class);
    }

    @Test
    void rejectsAMalformedSignatureHeader() {
        String rawJson = "{\"id_pedido\":\"" + UUID.randomUUID() + "\",\"enum_status\":9,\"status\":\"Comprado\"}";

        assertThatThrownBy(() -> ParceleMaisWebhookEvent.parse(rawJson, "não é um header válido", SIGNING_SECRET))
                .isInstanceOf(ParceleMaisWebhookSignatureException.class);
    }
}
