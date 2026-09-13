package twila.parcelemais.orders;

import twila.parcelemais.PagedResult;
import twila.parcelemais.config.ParceleMaisResilienceOptions;
import twila.parcelemais.internal.generated.PagedResultWire;
import twila.parcelemais.internal.generated.order.CreateOrderRequestWire;
import twila.parcelemais.internal.generated.order.IdentifierResponseWire;
import twila.parcelemais.internal.generated.order.ImportOrderInvoiceRequestWire;
import twila.parcelemais.internal.generated.order.LinkPaymentResponseWire;
import twila.parcelemais.internal.generated.order.OrderWire;
import twila.parcelemais.internal.generated.order.StartCdcSaleRequestWire;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.internal.http.QueryStringBuilder;
import twila.parcelemais.internal.mapping.OrderMapper;
import twila.parcelemais.orders.model.CheckoutLink;
import twila.parcelemais.orders.model.CreateOrderRequest;
import twila.parcelemais.orders.model.InvoiceFile;
import twila.parcelemais.orders.model.ListOrdersRequest;
import twila.parcelemais.orders.model.Order;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import java.util.UUID;
import java.util.stream.Collectors;

public final class OrdersClientImpl implements OrdersClient {

    private final ApiRequestExecutor executor;
    private final ParceleMaisResilienceOptions resilienceOptions;

    public OrdersClientImpl(ApiRequestExecutor executor, ParceleMaisResilienceOptions resilienceOptions) {
        this.executor = executor;
        this.resilienceOptions = resilienceOptions;
    }

    @Override
    public UUID create(CreateOrderRequest request) {
        CreateOrderRequestWire wireRequest = OrderMapper.toWire(request);
        ApiResponse response = executor.post("v1/order", writeJson(wireRequest));
        ApiRequestExecutor.ensureSuccess(response);

        IdentifierResponseWire identifier = readJson(response.body(), IdentifierResponseWire.class);
        return identifier.pedidoId;
    }

    @Override
    public Order get(UUID orderId) {
        ApiResponse response = executor.get("v1/order/" + orderId);
        ApiRequestExecutor.ensureSuccess(response);

        return OrderMapper.toPublic(readJson(response.body(), OrderWire.class));
    }

    @Override
    public PagedResult<Order> list(ListOrdersRequest request) {
        String path = new QueryStringBuilder()
                .add("status", request.getStatus() != null ? request.getStatus().wireValue() : null)
                .add("documentoCliente", request.getCustomerDocument())
                .add("dataInicio", request.getStartDate())
                .add("dataFim", request.getEndDate())
                .add("numero", request.getNumber())
                .add("documentoLoja", request.getEstablishmentDocument())
                .add("descricao", request.getDescription())
                .add("pagina", request.getPage())
                .add("tamanhoPagina", request.getPageSize())
                .build("v1/order/paged");

        ApiResponse response = executor.get(path);
        ApiRequestExecutor.ensureSuccess(response);

        PagedResultWire<OrderWire> wire = readJson(response.body(), ParceleMaisObjectMapper.DEFAULT.getTypeFactory()
                .constructParametricType(PagedResultWire.class, OrderWire.class));

        return new PagedResult<>(
                wire.itens.stream().map(OrderMapper::toPublic).collect(Collectors.toList()),
                wire.pagina.temProximo,
                wire.pagina.temAnterior,
                wire.pagina.numero,
                wire.pagina.tamanho,
                wire.pagina.total);
    }

    @Override
    public CheckoutLink startCdcSale(UUID orderId) {
        StartCdcSaleRequestWire wireRequest = new StartCdcSaleRequestWire(orderId);
        ApiResponse response = executor.post("v1/order/start-cdc-sale", writeJson(wireRequest));
        ApiRequestExecutor.ensureSuccess(response);

        LinkPaymentResponseWire wire = readJson(response.body(), LinkPaymentResponseWire.class);
        return new CheckoutLink(wire.linkPagamento);
    }

    @Override
    public void importInvoice(UUID orderId, InvoiceFile file) {
        ImportOrderInvoiceRequestWire wireRequest = new ImportOrderInvoiceRequestWire(orderId, file.getBase64Content(), file.getFileName());
        ApiResponse response = executor.post("v1/order/invoice", writeJson(wireRequest), resilienceOptions.getInvoiceUploadAttemptTimeout());
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

    private static <T> T readJson(String body, com.fasterxml.jackson.databind.JavaType type) {
        try {
            return ParceleMaisObjectMapper.DEFAULT.readValue(body, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao desserializar a resposta da API.", ex);
        }
    }
}
