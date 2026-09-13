package twila.parcelemais.orders;

import twila.parcelemais.PagedResult;
import twila.parcelemais.orders.model.CheckoutLink;
import twila.parcelemais.orders.model.CreateOrderRequest;
import twila.parcelemais.orders.model.InvoiceFile;
import twila.parcelemais.orders.model.ListOrdersRequest;
import twila.parcelemais.orders.model.Order;
import java.util.UUID;

public interface OrdersClient {

    UUID create(CreateOrderRequest request);

    Order get(UUID orderId);

    default PagedResult<Order> list() {
        return list(ListOrdersRequest.builder().build());
    }

    PagedResult<Order> list(ListOrdersRequest request);

    CheckoutLink startCdcSale(UUID orderId);

    void importInvoice(UUID orderId, InvoiceFile file);
}
