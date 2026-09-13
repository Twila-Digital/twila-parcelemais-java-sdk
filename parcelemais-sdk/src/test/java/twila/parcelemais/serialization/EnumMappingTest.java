package twila.parcelemais.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import twila.parcelemais.orders.model.OrderStatus;
import twila.parcelemais.webhooks.model.WebHookAuthenticationType;
import org.junit.jupiter.api.Test;

class EnumMappingTest {

    @Test
    void mapsKnownWireValue() {
        assertThat(OrderStatus.fromWireValue(9)).isEqualTo(OrderStatus.PURCHASED);
    }

    @Test
    void fallsBackToUnknownForUnrecognizedWireValue() {
        assertThat(OrderStatus.fromWireValue(9999)).isEqualTo(OrderStatus.UNKNOWN);
    }

    @Test
    void unknownFallbackAppliesToEveryWireEnum() {
        assertThat(WebHookAuthenticationType.fromWireValue(-42)).isEqualTo(WebHookAuthenticationType.UNKNOWN);
    }

    @Test
    void wireValueRoundTrips() {
        for (OrderStatus status : OrderStatus.values())
            assertThat(OrderStatus.fromWireValue(status.wireValue())).isEqualTo(status);
    }
}
