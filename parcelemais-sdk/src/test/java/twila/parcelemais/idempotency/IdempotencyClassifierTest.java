package twila.parcelemais.idempotency;

import static org.assertj.core.api.Assertions.assertThat;

import twila.parcelemais.internal.idempotency.IdempotencyClassifier;
import org.junit.jupiter.api.Test;

class IdempotencyClassifierTest {

    @Test
    void getIsAlwaysRetrySafe() {
        assertThat(IdempotencyClassifier.isRetrySafe("GET", "v1/order/paged", false)).isTrue();
    }

    @Test
    void postToOrderRequiresIdempotencyKey() {
        assertThat(IdempotencyClassifier.requiresIdempotencyKey("POST", "v1/order")).isTrue();
    }

    @Test
    void postToSimulateInstallmentsDoesNotRequireIdempotencyKey() {
        assertThat(IdempotencyClassifier.requiresIdempotencyKey("GET", "v1/order/simulate-installments")).isFalse();
    }

    @Test
    void postToOrderIsRetrySafeOnlyWhenIdempotencyKeyPresent() {
        assertThat(IdempotencyClassifier.isRetrySafe("POST", "v1/order", true)).isTrue();
        assertThat(IdempotencyClassifier.isRetrySafe("POST", "v1/order", false)).isFalse();
    }

    @Test
    void putAndDeleteOnWebhooksAreRetrySafe() {
        assertThat(IdempotencyClassifier.isRetrySafe("PUT", "v1/webhooks/3", false)).isTrue();
        assertThat(IdempotencyClassifier.isRetrySafe("DELETE", "v1/webhooks/3", false)).isTrue();
    }

    @Test
    void putOnOrderIsNotRetrySafe() {
        assertThat(IdempotencyClassifier.isRetrySafe("PUT", "v1/order/123", false)).isFalse();
    }
}
