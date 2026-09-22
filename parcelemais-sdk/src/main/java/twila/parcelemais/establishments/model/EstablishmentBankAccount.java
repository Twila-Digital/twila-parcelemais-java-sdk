package twila.parcelemais.establishments.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EstablishmentBankAccount {
    String bankNumber;
    String agencyNumber;
    String agencyDigit;
    String accountNumber;
    String accountDigit;
    BankAccountType accountType;
    String holderName;
    String holderDocument;
}
