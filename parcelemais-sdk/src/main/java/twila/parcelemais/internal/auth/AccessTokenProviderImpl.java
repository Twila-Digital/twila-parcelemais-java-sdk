package twila.parcelemais.internal.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public final class AccessTokenProviderImpl implements AccessTokenProvider {

    private static final Duration CLOCK_SKEW = Duration.ofSeconds(60);

    private final TokenApiClient tokenApiClient;
    private final String clientId;
    private final String clientSecret;
    private final ReentrantLock lock = new ReentrantLock();

    private volatile AccessToken cached;

    public AccessTokenProviderImpl(TokenApiClient tokenApiClient, String clientId, String clientSecret) {
        this.tokenApiClient = tokenApiClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public String getToken() {
        AccessToken current = cached;
        if (current != null && !current.isCloseToExpiry(CLOCK_SKEW))
            return current.value();

        lock.lock();
        try {
            current = cached;
            if (current != null && !current.isCloseToExpiry(CLOCK_SKEW))
                return current.value();

            GenerateAccessTokenResponse response = tokenApiClient.generate(clientId, clientSecret);
            AccessToken fresh = new AccessToken(response.getAccessToken(), Instant.now().plusSeconds(response.getExpiresInSeconds()));

            cached = fresh;
            return fresh.value();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void invalidate() {
        cached = null;
    }
}
