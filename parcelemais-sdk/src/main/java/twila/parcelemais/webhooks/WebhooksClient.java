package twila.parcelemais.webhooks;

import twila.parcelemais.webhooks.model.CreateWebhookRequest;
import twila.parcelemais.webhooks.model.CreateWebhookResult;
import twila.parcelemais.webhooks.model.UpdateWebhookRequest;
import twila.parcelemais.webhooks.model.WebHookType;
import twila.parcelemais.webhooks.model.Webhook;
import java.util.List;

public interface WebhooksClient {

    CreateWebhookResult create(CreateWebhookRequest request);

    List<Webhook> list();

    void update(WebHookType type, UpdateWebhookRequest request);

    void delete(WebHookType type);
}
