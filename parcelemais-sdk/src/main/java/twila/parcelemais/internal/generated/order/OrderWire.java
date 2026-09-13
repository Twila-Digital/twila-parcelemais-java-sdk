package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderWire {

    @JsonProperty("id")
    public final UUID id;

    @JsonProperty("numero")
    public final long numero;

    @JsonProperty("status")
    public final OrderStatusWire status;

    @JsonProperty("documentoCliente")
    public final String documentoCliente;

    @JsonProperty("razaoSocialEstabelecimento")
    public final String razaoSocialEstabelecimento;

    @JsonProperty("documentoEstabelecimento")
    public final String documentoEstabelecimento;

    @JsonProperty("criadoEm")
    public final OffsetDateTime criadoEm;

    @JsonProperty("total")
    public final BigDecimal total;

    @JsonProperty("nomeCliente")
    public final String nomeCliente;

    @JsonProperty("prazo")
    public final Integer prazo;

    @JsonProperty("descricao")
    public final String descricao;

    @JsonProperty("valorAprovado")
    public final BigDecimal valorAprovado;

    @JsonProperty("desembolsado")
    public final Boolean desembolsado;

    @JsonProperty("desembolsadoEm")
    public final OffsetDateTime desembolsadoEm;

    @JsonProperty("valorSolicitado")
    public final BigDecimal valorSolicitado;

    @JsonCreator
    public OrderWire(
            @JsonProperty("id") UUID id,
            @JsonProperty("numero") long numero,
            @JsonProperty("status") OrderStatusWire status,
            @JsonProperty("documentoCliente") String documentoCliente,
            @JsonProperty("razaoSocialEstabelecimento") String razaoSocialEstabelecimento,
            @JsonProperty("documentoEstabelecimento") String documentoEstabelecimento,
            @JsonProperty("criadoEm") OffsetDateTime criadoEm,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("nomeCliente") String nomeCliente,
            @JsonProperty("prazo") Integer prazo,
            @JsonProperty("descricao") String descricao,
            @JsonProperty("valorAprovado") BigDecimal valorAprovado,
            @JsonProperty("desembolsado") Boolean desembolsado,
            @JsonProperty("desembolsadoEm") OffsetDateTime desembolsadoEm,
            @JsonProperty("valorSolicitado") BigDecimal valorSolicitado) {
        this.id = id;
        this.numero = numero;
        this.status = status;
        this.documentoCliente = documentoCliente;
        this.razaoSocialEstabelecimento = razaoSocialEstabelecimento;
        this.documentoEstabelecimento = documentoEstabelecimento;
        this.criadoEm = criadoEm;
        this.total = total;
        this.nomeCliente = nomeCliente;
        this.prazo = prazo;
        this.descricao = descricao;
        this.valorAprovado = valorAprovado;
        this.desembolsado = desembolsado;
        this.desembolsadoEm = desembolsadoEm;
        this.valorSolicitado = valorSolicitado;
    }
}
