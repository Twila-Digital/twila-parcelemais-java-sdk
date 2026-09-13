package twila.parcelemais.orders.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Order {
    UUID id;
    long number;
    OrderStatus status;
    String statusDescription;
    String customerDocument;
    String establishmentLegalName;
    String establishmentDocument;
    OffsetDateTime createdAt;
    BigDecimal total;
    String customerName;
    Integer term;
    String description;
    BigDecimal approvedAmount;
    Boolean disbursed;
    OffsetDateTime disbursedAt;
    BigDecimal requestedAmount;
}
