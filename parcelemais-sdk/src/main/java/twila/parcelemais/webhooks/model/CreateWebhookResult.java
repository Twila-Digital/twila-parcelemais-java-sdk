package twila.parcelemais.webhooks.model;

import lombok.Value;

@Value
public class CreateWebhookResult {
    String signingSecret;
}
