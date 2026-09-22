package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateEstablishmentStatusRequestWire {

    @JsonProperty("ativa")
    public final boolean ativa;

    public UpdateEstablishmentStatusRequestWire(boolean ativa) {
        this.ativa = ativa;
    }
}
