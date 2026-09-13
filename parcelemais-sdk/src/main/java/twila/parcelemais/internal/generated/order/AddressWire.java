package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AddressWire {

    @JsonProperty("logradouro")
    public final String logradouro;

    @JsonProperty("numero")
    public final String numero;

    @JsonProperty("bairro")
    public final String bairro;

    @JsonProperty("cidade")
    public final String cidade;

    @JsonProperty("estado")
    public final String estado;

    @JsonProperty("cep")
    public final String cep;

    @JsonProperty("complemento")
    public final String complemento;

    @JsonCreator
    public AddressWire(
            @JsonProperty("logradouro") String logradouro,
            @JsonProperty("numero") String numero,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("cidade") String cidade,
            @JsonProperty("estado") String estado,
            @JsonProperty("cep") String cep,
            @JsonProperty("complemento") String complemento) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.complemento = complemento;
    }
}
