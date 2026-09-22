package twila.parcelemais.establishments.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateEstablishmentRequest {
    String document;
    String legalName;
    String tradeName;
    DisbursementModel disbursementModel;
    EstablishmentOwner owner;
    EstablishmentBankAccount bankAccount;
    EstablishmentAddress address;
}
