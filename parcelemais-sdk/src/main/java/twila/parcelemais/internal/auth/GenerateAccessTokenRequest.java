package twila.parcelemais.internal.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

final class GenerateAccessTokenRequest {

    @JsonProperty("clientId")
    private final String clientId;

    @JsonProperty("clientSecret")
    private final String clientSecret;

    GenerateAccessTokenRequest(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
