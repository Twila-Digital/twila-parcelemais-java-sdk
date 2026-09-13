package twila.parcelemais.webhooks.model;

import twila.parcelemais.orders.model.OrderStatus;
import java.util.UUID;
import lombok.Value;

@Value
public class OrderWebhookEvent {
    UUID orderId;
    OrderStatus status;
    int statusRaw;
    String statusName;
}
