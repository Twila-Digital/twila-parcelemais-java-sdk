package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EstablishmentOwnerWire {

    @JsonProperty("nome")
    public final String nome;

    @JsonProperty("email")
    public final String email;

    @JsonProperty("celular")
    public final String celular;

    @JsonCreator
    public EstablishmentOwnerWire(
            @JsonProperty("nome") String nome,
            @JsonProperty("email") String email,
            @JsonProperty("celular") String celular) {
        this.nome = nome;
        this.email = email;
        this.celular = celular;
    }
}
