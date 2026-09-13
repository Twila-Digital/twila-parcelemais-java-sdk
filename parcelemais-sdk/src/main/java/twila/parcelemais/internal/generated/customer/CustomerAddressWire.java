package twila.parcelemais.internal.generated.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CustomerAddressWire {

    @JsonProperty("rua")
    public final String rua;

    @JsonProperty("cidade")
    public final String cidade;

    @JsonProperty("estado")
    public final String estado;

    @JsonProperty("bairro")
    public final String bairro;

    @JsonProperty("cep")
    public final String cep;

    @JsonProperty("pais")
    public final String pais;

    @JsonProperty("numero")
    public final String numero;

    @JsonProperty("complemento")
    public final String complemento;

    @JsonCreator
    public CustomerAddressWire(
            @JsonProperty("rua") String rua,
            @JsonProperty("cidade") String cidade,
            @JsonProperty("estado") String estado,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("cep") String cep,
            @JsonProperty("pais") String pais,
            @JsonProperty("numero") String numero,
            @JsonProperty("complemento") String complemento) {
        this.rua = rua;
        this.cidade = cidade;
        this.estado = estado;
        this.bairro = bairro;
        this.cep = cep;
        this.pais = pais;
        this.numero = numero;
        this.complemento = complemento;
    }
}
