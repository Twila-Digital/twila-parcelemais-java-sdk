package twila.parcelemais.internal.auth;

public interface AccessTokenProvider {

    String getToken();

    void invalidate();
}
