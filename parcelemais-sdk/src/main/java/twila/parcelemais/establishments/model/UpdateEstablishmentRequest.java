package twila.parcelemais.establishments.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateEstablishmentRequest {
    String tradeName;
    DisbursementModel disbursementModel;
    EstablishmentAddress address;
}
