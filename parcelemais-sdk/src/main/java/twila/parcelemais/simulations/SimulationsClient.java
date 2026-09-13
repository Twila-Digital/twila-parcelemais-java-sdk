package twila.parcelemais.simulations;

import twila.parcelemais.simulations.model.InstallmentSimulation;
import twila.parcelemais.simulations.model.SimulateInstallmentsRequest;
import twila.parcelemais.simulations.model.SimulateValuesRequest;
import twila.parcelemais.simulations.model.ValuesSimulation;
import java.util.List;

public interface SimulationsClient {

    List<InstallmentSimulation> simulateInstallments(SimulateInstallmentsRequest request);

    ValuesSimulation simulateValues(SimulateValuesRequest request);
}
