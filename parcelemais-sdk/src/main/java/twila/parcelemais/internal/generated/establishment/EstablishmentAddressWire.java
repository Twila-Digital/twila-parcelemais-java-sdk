package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EstablishmentAddressWire {

    @JsonProperty("rua")
    public final String rua;

    @JsonProperty("numero")
    public final String numero;

    @JsonProperty("complemento")
    public final String complemento;

    @JsonProperty("bairro")
    public final String bairro;

    @JsonProperty("cidade")
    public final String cidade;

    @JsonProperty("estado")
    public final String estado;

    @JsonProperty("cep")
    public final String cep;

    @JsonProperty("pais")
    public final String pais;

    @JsonCreator
    public EstablishmentAddressWire(
            @JsonProperty("rua") String rua,
            @JsonProperty("numero") String numero,
            @JsonProperty("complemento") String complemento,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("cidade") String cidade,
            @JsonProperty("estado") String estado,
            @JsonProperty("cep") String cep,
            @JsonProperty("pais") String pais) {
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.pais = pais;
    }
}
