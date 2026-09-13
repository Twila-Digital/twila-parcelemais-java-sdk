package twila.parcelemais.config;

import java.net.URI;

/**
 * Ambiente da API do Parcele+ a ser utilizado pelo client.
 */
public enum ParceleMaisEnvironment {

    /**
     * Ambiente de staging ({@code https://api.staging.parcelemais.com.br/integration}).
     */
    STAGING("https://api.staging.parcelemais.com.br/integration/"),

    /**
     * Ambiente de produção ({@code https://api.parcelemais.com.br/integration}).
     */
    PRODUCTION("https://api.parcelemais.com.br/integration/");

    private final URI baseUri;

    ParceleMaisEnvironment(String baseUrl) {
        this.baseUri = URI.create(baseUrl);
    }

    public URI toBaseUri() {
        return baseUri;
    }
}
