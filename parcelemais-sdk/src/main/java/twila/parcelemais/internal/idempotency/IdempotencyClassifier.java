package twila.parcelemais.internal.idempotency;

public final class IdempotencyClassifier {

    private static final String[] MUTABLE_PATHS_REQUIRING_IDEMPOTENCY_KEY = {
            "v1/order/start-cdc-sale",
            "v1/order/invoice",
            "v1/order",
            "v1/webhooks"
    };

    private IdempotencyClassifier() {
    }

    public static boolean requiresIdempotencyKey(String method, String path) {
        if (!"POST".equalsIgnoreCase(method))
            return false;

        for (String mutablePath : MUTABLE_PATHS_REQUIRING_IDEMPOTENCY_KEY) {
            if (path.endsWith(mutablePath))
                return true;
        }

        return false;
    }

    public static boolean isRetrySafe(String method, String path, boolean hasIdempotencyKey) {
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method))
            return true;

        if ("PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))
            return path.contains("v1/webhooks/");

        if (!"POST".equalsIgnoreCase(method))
            return false;

        return requiresIdempotencyKey(method, path) && hasIdempotencyKey;
    }
}
