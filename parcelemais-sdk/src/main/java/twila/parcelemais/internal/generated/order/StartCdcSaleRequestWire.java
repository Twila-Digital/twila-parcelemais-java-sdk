package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public final class StartCdcSaleRequestWire {

    @JsonProperty("pedidoId")
    public final UUID pedidoId;

    public StartCdcSaleRequestWire(UUID pedidoId) {
        this.pedidoId = pedidoId;
    }
}
