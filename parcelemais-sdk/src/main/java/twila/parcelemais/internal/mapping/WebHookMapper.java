package twila.parcelemais.internal.mapping;

import twila.parcelemais.internal.generated.webhook.CreateWebHookRequestWire;
import twila.parcelemais.internal.generated.webhook.UpdateWebHookRequestWire;
import twila.parcelemais.internal.generated.webhook.WebHookAuditWire;
import twila.parcelemais.internal.generated.webhook.WebHookWire;
import twila.parcelemais.webhooks.model.CreateWebhookRequest;
import twila.parcelemais.webhooks.model.UpdateWebhookRequest;
import twila.parcelemais.webhooks.model.Webhook;
import twila.parcelemais.webhooks.model.WebhookAudit;
import twila.parcelemais.webhooks.model.WebHookAuthenticationType;
import twila.parcelemais.webhooks.model.WebHookType;

public final class WebHookMapper {

    private WebHookMapper() {
    }

    public static Webhook toPublic(WebHookWire wire) {
        return new Webhook(
                WebHookType.fromWireValue(wire.tipo),
                wire.url,
                WebHookAuthenticationType.fromWireValue(wire.tipoAutenticacao));
    }

    public static WebhookAudit toPublic(WebHookAuditWire wire) {
        return WebhookAudit.builder()
                .id(wire.id)
                .type(WebHookType.fromWireValue(wire.tipo))
                .request(wire.requisicao)
                .response(wire.resposta)
                .statusCode(wire.statusCode)
                .createdAt(wire.dataCriacao)
                .build();
    }

    public static CreateWebHookRequestWire toWire(CreateWebhookRequest request) {
        return new CreateWebHookRequestWire(
                request.getType().wireValue(),
                request.getUrl(),
                request.getAuthenticationType().wireValue(),
                request.getCredential());
    }

    public static UpdateWebHookRequestWire toWire(UpdateWebhookRequest request) {
        return new UpdateWebHookRequestWire(
                request.getUrl(),
                request.getAuthenticationType().wireValue(),
                request.getCredential());
    }
}
