package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateEstablishmentRequestWire {

    @JsonProperty("nomeFantasia")
    public final String nomeFantasia;

    @JsonProperty("modeloDesembolso")
    public final Integer modeloDesembolso;

    @JsonProperty("endereco")
    public final EstablishmentAddressWire endereco;

    public UpdateEstablishmentRequestWire(
            String nomeFantasia,
            Integer modeloDesembolso,
            EstablishmentAddressWire endereco) {
        this.nomeFantasia = nomeFantasia;
        this.modeloDesembolso = modeloDesembolso;
        this.endereco = endereco;
    }
}
