package twila.parcelemais.internal.auth;

import java.time.Duration;
import java.time.Instant;

final class AccessToken {

    private final String value;
    private final Instant expiresAt;

    AccessToken(String value, Instant expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    String value() {
        return value;
    }

    boolean isCloseToExpiry(Duration clockSkew) {
        return isCloseToExpiry(clockSkew, Instant.now());
    }

    boolean isCloseToExpiry(Duration clockSkew, Instant now) {
        return now.plus(clockSkew).compareTo(expiresAt) >= 0;
    }
}
