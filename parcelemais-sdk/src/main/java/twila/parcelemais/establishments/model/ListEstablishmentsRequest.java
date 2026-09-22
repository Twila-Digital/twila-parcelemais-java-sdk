package twila.parcelemais.establishments.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListEstablishmentsRequest {
    String tradeName;
    Boolean isActive;
}
