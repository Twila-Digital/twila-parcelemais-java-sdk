package twila.parcelemais.internal.resilience;

import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.internal.http.ApiResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class TransientFailureClassifier {

    private static final Set<Integer> TRANSIENT_STATUS_CODES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(408, 429, 502, 503, 504)));

    private TransientFailureClassifier() {
    }

    static boolean isTransientResponse(ApiResponse response, ParceleMaisResilienceOptions options) {
        int statusCode = response.statusCode();

        if (TRANSIENT_STATUS_CODES.contains(statusCode))
            return true;

        return statusCode == 500 && options.isRetryOn500();
    }

    static boolean isTransientException(Throwable ex) {
        return ex instanceof IOException;
    }
}
