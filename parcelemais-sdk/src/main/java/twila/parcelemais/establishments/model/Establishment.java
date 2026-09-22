package twila.parcelemais.establishments.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Establishment {
    UUID establishmentId;
    String document;
    String legalName;
    String tradeName;
    boolean isActive;
    EstablishmentOwner owner;
    DisbursementModel disbursementModel;
    EstablishmentBankAccount bankAccount;
    EstablishmentAddress address;
}
