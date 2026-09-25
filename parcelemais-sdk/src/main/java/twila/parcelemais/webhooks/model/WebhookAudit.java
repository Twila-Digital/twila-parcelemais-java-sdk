package twila.parcelemais.webhooks.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WebhookAudit {
    UUID id;
    WebHookType type;
    String request;
    String response;
    int statusCode;
    OffsetDateTime createdAt;
}
