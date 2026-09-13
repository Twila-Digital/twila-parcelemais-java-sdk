package twila.parcelemais.orders.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Address {
    String street;
    String number;
    String neighborhood;
    String city;
    String state;
    String postalCode;
    String complement;
}
