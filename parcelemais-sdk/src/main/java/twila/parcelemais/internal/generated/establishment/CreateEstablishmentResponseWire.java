package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateEstablishmentResponseWire {

    @JsonProperty("estabelecimentoId")
    public final UUID estabelecimentoId;

    @JsonCreator
    public CreateEstablishmentResponseWire(@JsonProperty("estabelecimentoId") UUID estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }
}
