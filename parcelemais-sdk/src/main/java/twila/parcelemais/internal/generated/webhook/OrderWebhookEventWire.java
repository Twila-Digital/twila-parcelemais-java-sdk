package twila.parcelemais.internal.generated.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderWebhookEventWire {

    @JsonProperty("id_pedido")
    public final UUID idPedido;

    @JsonProperty("enum_status")
    public final int enumStatus;

    @JsonProperty("status")
    public final String status;

    @JsonCreator
    public OrderWebhookEventWire(
            @JsonProperty("id_pedido") UUID idPedido,
            @JsonProperty("enum_status") int enumStatus,
            @JsonProperty("status") String status) {
        this.idPedido = idPedido;
        this.enumStatus = enumStatus;
        this.status = status;
    }
}
