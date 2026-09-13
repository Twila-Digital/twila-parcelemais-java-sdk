package twila.parcelemais.internal.generated.webhook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class WebHookWire {

    @JsonProperty("tipo")
    public final int tipo;

    @JsonProperty("url")
    public final String url;

    @JsonProperty("tipoAutenticacao")
    public final int tipoAutenticacao;

    @JsonCreator
    public WebHookWire(
            @JsonProperty("tipo") int tipo,
            @JsonProperty("url") String url,
            @JsonProperty("tipoAutenticacao") int tipoAutenticacao) {
        this.tipo = tipo;
        this.url = url;
        this.tipoAutenticacao = tipoAutenticacao;
    }
}
