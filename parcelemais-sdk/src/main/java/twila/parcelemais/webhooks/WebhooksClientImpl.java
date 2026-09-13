package twila.parcelemais.webhooks;

import twila.parcelemais.internal.generated.webhook.CreateWebHookRequestWire;
import twila.parcelemais.internal.generated.webhook.CreateWebHookResponseWire;
import twila.parcelemais.internal.generated.webhook.UpdateWebHookRequestWire;
import twila.parcelemais.internal.generated.webhook.WebHookWire;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.internal.mapping.WebHookMapper;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import twila.parcelemais.webhooks.model.CreateWebhookRequest;
import twila.parcelemais.webhooks.model.CreateWebhookResult;
import twila.parcelemais.webhooks.model.UpdateWebhookRequest;
import twila.parcelemais.webhooks.model.WebHookType;
import twila.parcelemais.webhooks.model.Webhook;
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
}
