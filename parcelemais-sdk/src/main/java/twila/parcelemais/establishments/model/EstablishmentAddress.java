package twila.parcelemais.establishments.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EstablishmentAddress {
    String street;
    String number;
    String complement;
    String district;
    String city;
    String state;
    String zipCode;
    String country;
}
