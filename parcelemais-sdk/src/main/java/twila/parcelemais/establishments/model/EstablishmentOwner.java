package twila.parcelemais.establishments.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EstablishmentOwner {
    String name;
    String email;
    String phone;
}
