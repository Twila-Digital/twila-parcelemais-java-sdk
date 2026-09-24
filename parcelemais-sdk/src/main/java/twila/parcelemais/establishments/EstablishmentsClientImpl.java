package twila.parcelemais.establishments;

import twila.parcelemais.internal.generated.establishment.CreateEstablishmentRequestWire;
import twila.parcelemais.internal.generated.establishment.CreateEstablishmentResponseWire;
import twila.parcelemais.internal.generated.establishment.EstablishmentWire;
import twila.parcelemais.internal.generated.establishment.UpdateEstablishmentRequestWire;
import twila.parcelemais.internal.generated.establishment.UpdateEstablishmentStatusRequestWire;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.internal.http.QueryStringBuilder;
import twila.parcelemais.internal.mapping.EstablishmentMapper;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import twila.parcelemais.establishments.model.CreateEstablishmentRequest;
import twila.parcelemais.establishments.model.Establishment;
import twila.parcelemais.establishments.model.EstablishmentBankAccount;
import twila.parcelemais.establishments.model.ListEstablishmentsRequest;
import twila.parcelemais.establishments.model.UpdateEstablishmentRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class EstablishmentsClientImpl implements EstablishmentsClient {

    private final ApiRequestExecutor executor;

    public EstablishmentsClientImpl(ApiRequestExecutor executor) {
        this.executor = executor;
    }

    @Override
    public UUID create(CreateEstablishmentRequest request) {
        CreateEstablishmentRequestWire wireRequest = EstablishmentMapper.toWire(request);
        ApiResponse response = executor.post("v1/establishment", writeJson(wireRequest));
        ApiRequestExecutor.ensureSuccess(response);

        CreateEstablishmentResponseWire wire = readJson(response.body(), CreateEstablishmentResponseWire.class);
        return wire.estabelecimentoId;
    }

    @Override
    public Establishment get(UUID establishmentId) {
        ApiResponse response = executor.get("v1/establishment/" + establishmentId);
        ApiRequestExecutor.ensureSuccess(response);

        return EstablishmentMapper.toPublic(readJson(response.body(), EstablishmentWire.class));
    }

    @Override
    public List<Establishment> list(ListEstablishmentsRequest request) {
        String path = new QueryStringBuilder()
                .add("nomeFantasia", request == null ? null : request.getTradeName())
                .add("ativa", request == null || request.getIsActive() == null
                        ? null
                        : String.valueOf(request.getIsActive()))
                .build("v1/establishment/list");

        ApiResponse response = executor.get(path);
        ApiRequestExecutor.ensureSuccess(response);

        List<EstablishmentWire> wire = readJson(response.body(), ParceleMaisObjectMapper.DEFAULT.getTypeFactory()
                .constructCollectionType(List.class, EstablishmentWire.class));

        return wire.stream().map(EstablishmentMapper::toPublic).collect(Collectors.toList());
    }

    @Override
    public void update(UUID establishmentId, UpdateEstablishmentRequest request) {
        UpdateEstablishmentRequestWire wireRequest = EstablishmentMapper.toWire(request);
        ApiResponse response = executor.put("v1/establishment/" + establishmentId, writeJson(wireRequest));
        ApiRequestExecutor.ensureSuccess(response);
    }

    @Override
    public void updateBankAccount(UUID establishmentId, EstablishmentBankAccount bankAccount) {
        ApiResponse response = executor.put(
                "v1/establishment/" + establishmentId + "/bank-account",
                writeJson(EstablishmentMapper.toBankAccountWire(bankAccount)));
        ApiRequestExecutor.ensureSuccess(response);
    }

    @Override
    public void activate(UUID establishmentId) {
        setActive(establishmentId, true);
    }

    @Override
    public void deactivate(UUID establishmentId) {
        setActive(establishmentId, false);
    }

    private void setActive(UUID establishmentId, boolean isActive) {
        ApiResponse response = executor.put(
                "v1/establishment/" + establishmentId + "/status",
                writeJson(new UpdateEstablishmentStatusRequestWire(isActive)));
        ApiRequestExecutor.ensureSuccess(response);
    }

    private static String writeJson(Object value) {
        try {
            return ParceleMaisObjectMapper.DEFAULT.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao serializar o corpo da requisição.", ex);
        }
    }

    private static <T> T readJson(String body, Class<T> type) {
        try {
            return ParceleMaisObjectMapper.DEFAULT.readValue(body, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao desserializar a resposta da API.", ex);
        }
    }

    private static <T> T readJson(String body, com.fasterxml.jackson.databind.JavaType type) {
        try {
            return ParceleMaisObjectMapper.DEFAULT.readValue(body, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao desserializar a resposta da API.", ex);
        }
    }
}
