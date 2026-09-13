package twila.parcelemais.internal.generated.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class CreateWebHookRequestWire {

    @JsonProperty("tipo")
    public final int tipo;

    @JsonProperty("url")
    public final String url;

    @JsonProperty("tipoAutenticacao")
    public final int tipoAutenticacao;

    @JsonProperty("credencial")
    public final String credencial;

    public CreateWebHookRequestWire(int tipo, String url, int tipoAutenticacao, String credencial) {
        this.tipo = tipo;
        this.url = url;
        this.tipoAutenticacao = tipoAutenticacao;
        this.credencial = credencial;
    }
}
