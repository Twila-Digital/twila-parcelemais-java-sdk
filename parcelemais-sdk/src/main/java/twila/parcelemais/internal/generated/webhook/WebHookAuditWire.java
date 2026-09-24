package twila.parcelemais.internal.generated.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class WebHookAuditWire {

    @JsonProperty("id")
    public final UUID id;

    @JsonProperty("tipo")
    public final int tipo;

    @JsonProperty("requisicao")
    public final String requisicao;

    @JsonProperty("resposta")
    public final String resposta;

    @JsonProperty("statusCode")
    public final int statusCode;

    @JsonProperty("dataCriacao")
    public final OffsetDateTime dataCriacao;

    @JsonCreator
    public WebHookAuditWire(
            @JsonProperty("id") UUID id,
            @JsonProperty("tipo") int tipo,
            @JsonProperty("requisicao") String requisicao,
            @JsonProperty("resposta") String resposta,
            @JsonProperty("statusCode") int statusCode,
            @JsonProperty("dataCriacao") OffsetDateTime dataCriacao) {
        this.id = id;
        this.tipo = tipo;
        this.requisicao = requisicao;
        this.resposta = resposta;
        this.statusCode = statusCode;
        this.dataCriacao = dataCriacao;
    }
}
