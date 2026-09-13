package twila.parcelemais.simulations;

import twila.parcelemais.internal.generated.simulations.SimulateInstallmentWire;
import twila.parcelemais.internal.generated.simulations.SimulationValuesWire;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.internal.http.QueryStringBuilder;
import twila.parcelemais.internal.mapping.SimulationMapper;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import twila.parcelemais.simulations.model.InstallmentSimulation;
import twila.parcelemais.simulations.model.SimulateInstallmentsRequest;
import twila.parcelemais.simulations.model.SimulateValuesRequest;
import twila.parcelemais.simulations.model.ValuesSimulation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SimulationsClientImpl implements SimulationsClient {

    private final ApiRequestExecutor executor;

    public SimulationsClientImpl(ApiRequestExecutor executor) {
        this.executor = executor;
    }

    @Override
    public List<InstallmentSimulation> simulateInstallments(SimulateInstallmentsRequest request) {
        String path = new QueryStringBuilder()
                .add("valorSolicitado", request.getRequestedAmount())
                .add("tipoValorCalculo", request.getCalculationValueType().wireValue())
                .build("v1/order/simulate-installments");

        ApiResponse response = executor.get(path);
        ApiRequestExecutor.ensureSuccess(response);

        SimulateInstallmentWire[] wires = readJson(response.body(), SimulateInstallmentWire[].class);
        return Arrays.stream(wires).map(SimulationMapper::toPublic).collect(Collectors.toList());
    }

    @Override
    public ValuesSimulation simulateValues(SimulateValuesRequest request) {
        String path = new QueryStringBuilder()
                .add("valor", request.getAmount())
                .add("prazo", request.getTerm())
                .add("modeloJuros", 1)
                .add("tipoValorCalculo", request.getCalculationValueType().wireValue())
                .build("v1/order/simulate-values");

        ApiResponse response = executor.get(path);
        ApiRequestExecutor.ensureSuccess(response);

        return SimulationMapper.toPublic(readJson(response.body(), SimulationValuesWire.class));
    }

    private static <T> T readJson(String body, Class<T> type) {
        try {
            return ParceleMaisObjectMapper.DEFAULT.readValue(body, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao desserializar a resposta da API.", ex);
        }
    }
}
