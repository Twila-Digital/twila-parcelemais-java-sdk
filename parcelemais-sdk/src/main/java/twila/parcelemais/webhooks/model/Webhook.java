package twila.parcelemais.webhooks.model;

import lombok.Value;

@Value
public class Webhook {
    WebHookType type;
    String url;
    WebHookAuthenticationType authenticationType;
}
