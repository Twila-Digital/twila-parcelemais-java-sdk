package twila.parcelemais.internal.mapping;

import twila.parcelemais.internal.generated.customer.CustomerAddressWire;
import twila.parcelemais.internal.generated.customer.CustomerWire;
import twila.parcelemais.customers.model.Address;
import twila.parcelemais.customers.model.Customer;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static Customer toPublic(CustomerWire wire) {
        return Customer.builder()
                .id(wire.id)
                .name(wire.nome)
                .document(wire.documento)
                .dateOfBirth(wire.dataDeNascimento)
                .address(wire.endereco != null ? toPublic(wire.endereco) : null)
                .email(wire.email)
                .phoneNumber(wire.celular)
                .build();
    }

    public static Address toPublic(CustomerAddressWire wire) {
        return Address.builder()
                .street(wire.rua)
                .number(wire.numero)
                .neighborhood(wire.bairro)
                .city(wire.cidade)
                .state(wire.estado)
                .postalCode(wire.cep)
                .country(wire.pais)
                .complement(wire.complemento)
                .build();
    }
}
