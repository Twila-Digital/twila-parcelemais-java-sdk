package twila.parcelemais.internal.auth;

public interface TokenApiClient {

    GenerateAccessTokenResponse generate(String clientId, String clientSecret);
}
