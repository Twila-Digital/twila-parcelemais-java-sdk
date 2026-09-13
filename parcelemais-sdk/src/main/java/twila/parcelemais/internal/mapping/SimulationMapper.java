package twila.parcelemais.internal.mapping;

import twila.parcelemais.internal.generated.simulations.SimulateInstallmentWire;
import twila.parcelemais.internal.generated.simulations.SimulationValuesWire;
import twila.parcelemais.simulations.model.InstallmentSimulation;
import twila.parcelemais.simulations.model.ValuesSimulation;

public final class SimulationMapper {

    private SimulationMapper() {
    }

    public static InstallmentSimulation toPublic(SimulateInstallmentWire wire) {
        return new InstallmentSimulation(wire.valorTotalDebito, wire.prazo, wire.valorParcela);
    }

    public static ValuesSimulation toPublic(SimulationValuesWire wire) {
        return new ValuesSimulation(
                wire.valoresEstabelecimento.valorVenda,
                wire.valoresEstabelecimento.valorDesembolso,
                wire.valoresCliente.valorParcela);
    }
}
