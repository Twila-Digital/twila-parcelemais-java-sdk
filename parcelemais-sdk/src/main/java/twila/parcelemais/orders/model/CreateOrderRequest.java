package twila.parcelemais.orders.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateOrderRequest {
    String cpf;
    String phoneNumber;
    String establishmentDocument;
    BigDecimal requestedAmount;
    String name;
    String email;
    OffsetDateTime dateOfBirth;
    Address address;
}
