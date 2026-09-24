package twila.parcelemais.establishments;

import twila.parcelemais.establishments.model.CreateEstablishmentRequest;
import twila.parcelemais.establishments.model.Establishment;
import twila.parcelemais.establishments.model.EstablishmentBankAccount;
import twila.parcelemais.establishments.model.ListEstablishmentsRequest;
import twila.parcelemais.establishments.model.UpdateEstablishmentRequest;
import java.util.List;
import java.util.UUID;

public interface EstablishmentsClient {

    UUID create(CreateEstablishmentRequest request);

    Establishment get(UUID establishmentId);

    List<Establishment> list(ListEstablishmentsRequest request);

    void update(UUID establishmentId, UpdateEstablishmentRequest request);

    void updateBankAccount(UUID establishmentId, EstablishmentBankAccount bankAccount);

    void activate(UUID establishmentId);

    void deactivate(UUID establishmentId);
}
