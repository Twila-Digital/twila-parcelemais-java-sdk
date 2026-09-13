package twila.parcelemais.webhooks;

import twila.parcelemais.errors.ParceleMaisWebhookSignatureException;
import twila.parcelemais.internal.generated.webhook.OrderWebhookEventWire;
import twila.parcelemais.internal.util.HexUtility;
import twila.parcelemais.orders.model.OrderStatus;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import twila.parcelemais.webhooks.model.OrderWebhookEvent;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class ParceleMaisWebhookEvent {

    private static final Duration REPLAY_TOLERANCE = Duration.ofMinutes(5);
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private ParceleMaisWebhookEvent() {
    }

    public static OrderWebhookEvent parse(String rawJson) {
        OrderWebhookEventWire wire;
        try {
            wire = ParceleMaisObjectMapper.DEFAULT.readValue(rawJson, OrderWebhookEventWire.class);
        } catch (Exception ex) {
            throw new ParceleMaisWebhookSignatureException("O corpo do webhook está vazio ou não é um JSON válido.");
        }

        if (wire == null)
            throw new ParceleMaisWebhookSignatureException("O corpo do webhook está vazio ou não é um JSON válido.");

        return new OrderWebhookEvent(
                wire.idPedido,
                OrderStatus.fromWireValue(wire.enumStatus),
                wire.enumStatus,
                wire.status);
    }

    public static OrderWebhookEvent parse(String rawJson, String signatureHeader, String signingSecret) {
        verifySignature(rawJson, signatureHeader, signingSecret);
        return parse(rawJson);
    }

    static String computeSignature(String signingSecret, long timestamp, String payload) {
        String signedContent = timestamp + "." + payload;

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return HexUtility.toHexStringLower(mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Falha ao calcular a assinatura HMAC-SHA256.", ex);
        }
    }

    private static void verifySignature(String rawJson, String signatureHeader, String signingSecret) {
        SignatureHeader parsed = parseSignatureHeader(signatureHeader);
        String computedSignature = computeSignature(signingSecret, parsed.timestamp, rawJson);

        if (!MessageDigest.isEqual(
                computedSignature.getBytes(StandardCharsets.UTF_8),
                parsed.signature.getBytes(StandardCharsets.UTF_8)))
            throw new ParceleMaisWebhookSignatureException("A assinatura do webhook não confere.");

        Instant eventTime = Instant.ofEpochSecond(parsed.timestamp);
        if (Duration.between(eventTime, Instant.now()).abs().compareTo(REPLAY_TOLERANCE) > 0)
            throw new ParceleMaisWebhookSignatureException("O timestamp do webhook está fora da janela de tolerância — possível replay.");
    }

    private static SignatureHeader parseSignatureHeader(String signatureHeader) {
        Long timestamp = null;
        String signature = null;

        for (String part : signatureHeader.split(",")) {
            String[] pair = part.split("=", 2);
            if (pair.length != 2)
                continue;

            switch (pair[0].trim()) {
                case "t":
                    try {
                        timestamp = Long.parseLong(pair[1].trim());
                    } catch (NumberFormatException ignored) {
                        // tratado abaixo pelo null-check
                    }
                    break;
                case "v1":
                    signature = pair[1].trim().toLowerCase();
                    break;
                default:
                    break;
            }
        }

        if (timestamp == null || signature == null)
            throw new ParceleMaisWebhookSignatureException("Cabeçalho de assinatura malformado: '" + signatureHeader + "'.");

        return new SignatureHeader(timestamp, signature);
    }

    private static final class SignatureHeader {
        final long timestamp;
        final String signature;

        SignatureHeader(long timestamp, String signature) {
            this.timestamp = timestamp;
            this.signature = signature;
        }
    }
}
