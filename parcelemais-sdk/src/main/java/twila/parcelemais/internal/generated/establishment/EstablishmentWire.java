package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EstablishmentWire {

    @JsonProperty("estabelecimentoId")
    public final UUID estabelecimentoId;

    @JsonProperty("documento")
    public final String documento;

    @JsonProperty("razaoSocial")
    public final String razaoSocial;

    @JsonProperty("nomeFantasia")
    public final String nomeFantasia;

    @JsonProperty("ativa")
    public final boolean ativa;

    @JsonProperty("modeloDesembolso")
    public final Integer modeloDesembolso;

    @JsonProperty("responsavel")
    public final EstablishmentOwnerWire responsavel;

    @JsonProperty("contaBancaria")
    public final EstablishmentBankAccountWire contaBancaria;

    @JsonProperty("endereco")
    public final EstablishmentAddressWire endereco;

    @JsonCreator
    public EstablishmentWire(
            @JsonProperty("estabelecimentoId") UUID estabelecimentoId,
            @JsonProperty("documento") String documento,
            @JsonProperty("razaoSocial") String razaoSocial,
            @JsonProperty("nomeFantasia") String nomeFantasia,
            @JsonProperty("ativa") boolean ativa,
            @JsonProperty("modeloDesembolso") Integer modeloDesembolso,
            @JsonProperty("responsavel") EstablishmentOwnerWire responsavel,
            @JsonProperty("contaBancaria") EstablishmentBankAccountWire contaBancaria,
            @JsonProperty("endereco") EstablishmentAddressWire endereco) {
        this.estabelecimentoId = estabelecimentoId;
        this.documento = documento;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.ativa = ativa;
        this.modeloDesembolso = modeloDesembolso;
        this.responsavel = responsavel;
        this.contaBancaria = contaBancaria;
        this.endereco = endereco;
    }
}
