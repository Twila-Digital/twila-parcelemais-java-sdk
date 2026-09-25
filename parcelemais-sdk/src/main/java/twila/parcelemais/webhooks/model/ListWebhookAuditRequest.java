package twila.parcelemais.webhooks.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListWebhookAuditRequest {
    OffsetDateTime startDate;
    OffsetDateTime endDate;
    UUID orderId;
    Long orderNumber;
    Integer statusCode;

    @Builder.Default
    int page = 1;

    @Builder.Default
    int pageSize = 10;
}
