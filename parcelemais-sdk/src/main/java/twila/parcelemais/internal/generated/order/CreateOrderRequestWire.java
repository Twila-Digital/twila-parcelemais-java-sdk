package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class CreateOrderRequestWire {

    @JsonProperty("cpf")
    public final String cpf;

    @JsonProperty("celular")
    public final String celular;

    @JsonProperty("documentoEstabelecimento")
    public final String documentoEstabelecimento;

    @JsonProperty("valorSolicitado")
    public final BigDecimal valorSolicitado;

    @JsonProperty("nome")
    public final String nome;

    @JsonProperty("email")
    public final String email;

    @JsonProperty("dataDeNascimento")
    public final OffsetDateTime dataDeNascimento;

    @JsonProperty("endereco")
    public final AddressWire endereco;

    public CreateOrderRequestWire(
            String cpf,
            String celular,
            String documentoEstabelecimento,
            BigDecimal valorSolicitado,
            String nome,
            String email,
            OffsetDateTime dataDeNascimento,
            AddressWire endereco) {
        this.cpf = cpf;
        this.celular = celular;
        this.documentoEstabelecimento = documentoEstabelecimento;
        this.valorSolicitado = valorSolicitado;
        this.nome = nome;
        this.email = email;
        this.dataDeNascimento = dataDeNascimento;
        this.endereco = endereco;
    }
}
