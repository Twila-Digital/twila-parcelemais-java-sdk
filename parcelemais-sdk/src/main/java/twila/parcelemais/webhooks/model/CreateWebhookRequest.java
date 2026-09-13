package twila.parcelemais.webhooks.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateWebhookRequest {
    WebHookType type;
    String url;
    WebHookAuthenticationType authenticationType;
    String credential;
}
