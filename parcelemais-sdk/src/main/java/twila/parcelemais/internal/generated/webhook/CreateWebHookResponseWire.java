package twila.parcelemais.internal.generated.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateWebHookResponseWire {

    @JsonProperty("chaveAssinatura")
    public final String chaveAssinatura;

    @JsonCreator
    public CreateWebHookResponseWire(@JsonProperty("chaveAssinatura") String chaveAssinatura) {
        this.chaveAssinatura = chaveAssinatura;
    }
}
