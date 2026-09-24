package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class CreateEstablishmentRequestWire {

    @JsonProperty("documento")
    public final String documento;

    @JsonProperty("razaoSocial")
    public final String razaoSocial;

    @JsonProperty("nomeFantasia")
    public final String nomeFantasia;

    @JsonProperty("modeloDesembolso")
    public final int modeloDesembolso;

    @JsonProperty("responsavel")
    public final EstablishmentOwnerWire responsavel;

    @JsonProperty("contaBancaria")
    public final EstablishmentBankAccountWire contaBancaria;

    @JsonProperty("endereco")
    public final EstablishmentAddressWire endereco;

    public CreateEstablishmentRequestWire(
            String documento,
            String razaoSocial,
            String nomeFantasia,
            int modeloDesembolso,
            EstablishmentOwnerWire responsavel,
            EstablishmentBankAccountWire contaBancaria,
            EstablishmentAddressWire endereco) {
        this.documento = documento;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.modeloDesembolso = modeloDesembolso;
        this.responsavel = responsavel;
        this.contaBancaria = contaBancaria;
        this.endereco = endereco;
    }
}
