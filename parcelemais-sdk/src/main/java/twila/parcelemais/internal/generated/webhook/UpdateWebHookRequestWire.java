package twila.parcelemais.internal.generated.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateWebHookRequestWire {

    @JsonProperty("url")
    public final String url;

    @JsonProperty("tipoAutenticacao")
    public final int tipoAutenticacao;

    @JsonProperty("credencial")
    public final String credencial;

    public UpdateWebHookRequestWire(String url, int tipoAutenticacao, String credencial) {
        this.url = url;
        this.tipoAutenticacao = tipoAutenticacao;
        this.credencial = credencial;
    }
}
