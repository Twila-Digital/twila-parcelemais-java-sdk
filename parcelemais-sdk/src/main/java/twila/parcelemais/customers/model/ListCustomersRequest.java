package twila.parcelemais.customers.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListCustomersRequest {
    String name;
    String document;

    @Builder.Default
    int page = 1;

    @Builder.Default
    int pageSize = 10;
}
