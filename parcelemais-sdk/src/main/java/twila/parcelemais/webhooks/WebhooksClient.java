package twila.parcelemais.webhooks;

import twila.parcelemais.PagedResult;
import twila.parcelemais.webhooks.model.CreateWebhookRequest;
import twila.parcelemais.webhooks.model.CreateWebhookResult;
import twila.parcelemais.webhooks.model.ListWebhookAuditRequest;
import twila.parcelemais.webhooks.model.UpdateWebhookRequest;
import twila.parcelemais.webhooks.model.WebHookType;
import twila.parcelemais.webhooks.model.Webhook;
import twila.parcelemais.webhooks.model.WebhookAudit;
import java.util.List;

public interface WebhooksClient {

    CreateWebhookResult create(CreateWebhookRequest request);

    List<Webhook> list();

    void update(WebHookType type, UpdateWebhookRequest request);

    void delete(WebHookType type);

    default PagedResult<WebhookAudit> listAudit() {
        return listAudit(ListWebhookAuditRequest.builder().build());
    }

    PagedResult<WebhookAudit> listAudit(ListWebhookAuditRequest request);
}
