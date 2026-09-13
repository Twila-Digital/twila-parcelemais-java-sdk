package twila.parcelemais.customers.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Customer {
    UUID id;
    String name;
    String document;
    OffsetDateTime dateOfBirth;
    Address address;
    String email;
    String phoneNumber;
}
