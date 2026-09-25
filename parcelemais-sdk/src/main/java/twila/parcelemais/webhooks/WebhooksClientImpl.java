package twila.parcelemais.webhooks;

import twila.parcelemais.PagedResult;
import twila.parcelemais.internal.generated.PagedResultWire;
import twila.parcelemais.internal.generated.webhook.CreateWebHookRequestWire;
import twila.parcelemais.internal.generated.webhook.CreateWebHookResponseWire;
import twila.parcelemais.internal.generated.webhook.UpdateWebHookRequestWire;
import twila.parcelemais.internal.generated.webhook.WebHookAuditWire;
import twila.parcelemais.internal.generated.webhook.WebHookWire;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.internal.http.QueryStringBuilder;
import twila.parcelemais.internal.mapping.WebHookMapper;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import twila.parcelemais.webhooks.model.CreateWebhookRequest;
import twila.parcelemais.webhooks.model.CreateWebhookResult;
import twila.parcelemais.webhooks.model.ListWebhookAuditRequest;
import twila.parcelemais.webhooks.model.UpdateWebhookRequest;
import twila.parcelemais.webhooks.model.WebHookType;
import twila.parcelemais.webhooks.model.Webhook;
import twila.parcelemais.webhooks.model.WebhookAudit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class WebhooksClientImpl implements WebhooksClient {

    private final ApiRequestExecutor executor;

    public WebhooksClientImpl(ApiRequestExecutor executor) {
        this.executor = executor;
    }

    @Override
    public CreateWebhookResult create(CreateWebhookRequest request) {
        CreateWebHookRequestWire wireRequest = WebHookMapper.toWire(request);
        ApiResponse response = executor.post("v1/webhooks", writeJson(wireRequest));
        ApiRequestExecutor.ensureSuccess(response);

        CreateWebHookResponseWire wire = readJson(response.body(), CreateWebHookResponseWire.class);
        return new CreateWebhookResult(wire.chaveAssinatura);
    }

    @Override
    public List<Webhook> list() {
        ApiResponse response = executor.get("v1/webhooks");
        ApiRequestExecutor.ensureSuccess(response);

        WebHookWire[] wires = readJson(response.body(), WebHookWire[].class);
        return Arrays.stream(wires).map(WebHookMapper::toPublic).collect(Collectors.toList());
    }

    @Override
    public void update(WebHookType type, UpdateWebhookRequest request) {
        UpdateWebHookRequestWire wireRequest = WebHookMapper.toWire(request);
        ApiResponse response = executor.put("v1/webhooks/" + type.wireValue(), writeJson(wireRequest));
        ApiRequestExecutor.ensureSuccess(response);
    }

    @Override
    public void delete(WebHookType type) {
        ApiResponse response = executor.delete("v1/webhooks/" + type.wireValue());
        ApiRequestExecutor.ensureSuccess(response);
    }

    @Override
    public PagedResult<WebhookAudit> listAudit(ListWebhookAuditRequest request) {
        String path = new QueryStringBuilder()
                .add("dataInicio", request.getStartDate())
                .add("dataFim", request.getEndDate())
                .add("pedidoId", request.getOrderId() != null ? request.getOrderId().toString() : null)
                .add("numeroPedido", request.getOrderNumber())
                .add("statusCode", request.getStatusCode())
                .add("pagina", request.getPage())
                .add("tamanhoPagina", request.getPageSize())
                .build("v1/webhooks/auditoria");

        ApiResponse response = executor.get(path);
        ApiRequestExecutor.ensureSuccess(response);

        PagedResultWire<WebHookAuditWire> wire = readJson(response.body(), ParceleMaisObjectMapper.DEFAULT.getTypeFactory()
                .constructParametricType(PagedResultWire.class, WebHookAuditWire.class));

        return new PagedResult<>(
                wire.itens.stream().map(WebHookMapper::toPublic).collect(Collectors.toList()),
                wire.pagina.temProximo,
                wire.pagina.temAnterior,
                wire.pagina.numero,
                wire.pagina.tamanho,
                wire.pagina.total);
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
