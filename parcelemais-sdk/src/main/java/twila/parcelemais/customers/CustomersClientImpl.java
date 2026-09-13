package twila.parcelemais.customers;

import twila.parcelemais.PagedResult;
import twila.parcelemais.internal.generated.PagedResultWire;
import twila.parcelemais.internal.generated.customer.CustomerWire;
import twila.parcelemais.internal.http.ApiRequestExecutor;
import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.internal.http.QueryStringBuilder;
import twila.parcelemais.internal.mapping.CustomerMapper;
import twila.parcelemais.customers.model.Customer;
import twila.parcelemais.customers.model.ListCustomersRequest;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CustomersClientImpl implements CustomersClient {

    private final ApiRequestExecutor executor;

    public CustomersClientImpl(ApiRequestExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Customer get(UUID customerId) {
        ApiResponse response = executor.get("v1/customer/" + customerId);
        ApiRequestExecutor.ensureSuccess(response);

        return CustomerMapper.toPublic(readJson(response.body(), CustomerWire.class));
    }

    @Override
    public PagedResult<Customer> list(ListCustomersRequest request) {
        String path = new QueryStringBuilder()
                .add("nome", request.getName())
                .add("documento", request.getDocument())
                .add("pagina", request.getPage())
                .add("tamanhoPagina", request.getPageSize())
                .build("v1/customer/paged");

        ApiResponse response = executor.get(path);
        ApiRequestExecutor.ensureSuccess(response);

        PagedResultWire<CustomerWire> wire = readJson(response.body(), ParceleMaisObjectMapper.DEFAULT.getTypeFactory()
                .constructParametricType(PagedResultWire.class, CustomerWire.class));

        return new PagedResult<>(
                wire.itens.stream().map(CustomerMapper::toPublic).collect(Collectors.toList()),
                wire.pagina.temProximo,
                wire.pagina.temAnterior,
                wire.pagina.numero,
                wire.pagina.tamanho,
                wire.pagina.total);
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
