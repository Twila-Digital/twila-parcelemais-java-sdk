package twila.parcelemais.internal.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GenerateAccessTokenResponse {

    private final String accessToken;
    private final int expiresInSeconds;
    private final String tokenType;

    @JsonCreator
    public GenerateAccessTokenResponse(
            @JsonProperty("token_de_acesso") String accessToken,
            @JsonProperty("expira_em_segundos") int expiresInSeconds,
            @JsonProperty("tipo_de_token") String tokenType) {
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }
}
