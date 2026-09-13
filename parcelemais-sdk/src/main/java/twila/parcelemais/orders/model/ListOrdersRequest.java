package twila.parcelemais.orders.model;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListOrdersRequest {
    OrderStatus status;
    String customerDocument;
    OffsetDateTime startDate;
    OffsetDateTime endDate;
    Long number;
    String establishmentDocument;
    String description;

    @Builder.Default
    int page = 1;

    @Builder.Default
    int pageSize = 10;
}
