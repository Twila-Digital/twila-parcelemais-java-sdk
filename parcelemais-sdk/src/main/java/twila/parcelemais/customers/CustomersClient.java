package twila.parcelemais.customers;

import twila.parcelemais.PagedResult;
import twila.parcelemais.customers.model.Customer;
import twila.parcelemais.customers.model.ListCustomersRequest;
import java.util.UUID;

public interface CustomersClient {

    Customer get(UUID customerId);

    default PagedResult<Customer> list() {
        return list(ListCustomersRequest.builder().build());
    }

    PagedResult<Customer> list(ListCustomersRequest request);
}
