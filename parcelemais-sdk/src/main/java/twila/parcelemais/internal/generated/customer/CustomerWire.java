package twila.parcelemais.internal.generated.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CustomerWire {

    @JsonProperty("id")
    public final UUID id;

    @JsonProperty("nome")
    public final String nome;

    @JsonProperty("documento")
    public final String documento;

    @JsonProperty("dataDeNascimento")
    public final OffsetDateTime dataDeNascimento;

    @JsonProperty("endereco")
    public final CustomerAddressWire endereco;

    @JsonProperty("email")
    public final String email;

    @JsonProperty("celular")
    public final String celular;

    @JsonCreator
    public CustomerWire(
            @JsonProperty("id") UUID id,
            @JsonProperty("nome") String nome,
            @JsonProperty("documento") String documento,
            @JsonProperty("dataDeNascimento") OffsetDateTime dataDeNascimento,
            @JsonProperty("endereco") CustomerAddressWire endereco,
            @JsonProperty("email") String email,
            @JsonProperty("celular") String celular) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.dataDeNascimento = dataDeNascimento;
        this.endereco = endereco;
        this.email = email;
        this.celular = celular;
    }
}
