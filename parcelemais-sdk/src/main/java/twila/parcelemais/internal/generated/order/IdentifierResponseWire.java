package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class IdentifierResponseWire {

    @JsonProperty("pedidoId")
    public final UUID pedidoId;

    @JsonCreator
    public IdentifierResponseWire(@JsonProperty("pedidoId") UUID pedidoId) {
        this.pedidoId = pedidoId;
    }
}
