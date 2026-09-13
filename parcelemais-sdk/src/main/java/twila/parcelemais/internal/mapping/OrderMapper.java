package twila.parcelemais.internal.mapping;

import twila.parcelemais.internal.generated.order.AddressWire;
import twila.parcelemais.internal.generated.order.CreateOrderRequestWire;
import twila.parcelemais.internal.generated.order.OrderWire;
import twila.parcelemais.orders.model.Address;
import twila.parcelemais.orders.model.CreateOrderRequest;
import twila.parcelemais.orders.model.Order;
import twila.parcelemais.orders.model.OrderStatus;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static Order toPublic(OrderWire wire) {
        return Order.builder()
                .id(wire.id)
                .number(wire.numero)
                .status(OrderStatus.fromWireValue(wire.status.valor))
                .statusDescription(wire.status.descricao)
                .customerDocument(wire.documentoCliente)
                .establishmentLegalName(wire.razaoSocialEstabelecimento)
                .establishmentDocument(wire.documentoEstabelecimento)
                .createdAt(wire.criadoEm)
                .total(wire.total)
                .customerName(wire.nomeCliente)
                .term(wire.prazo)
                .description(wire.descricao)
                .approvedAmount(wire.valorAprovado)
                .disbursed(wire.desembolsado)
                .disbursedAt(wire.desembolsadoEm)
                .requestedAmount(wire.valorSolicitado)
                .build();
    }

    public static AddressWire toWire(Address address) {
        return new AddressWire(
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getComplement());
    }

    public static CreateOrderRequestWire toWire(CreateOrderRequest request) {
        return new CreateOrderRequestWire(
                request.getCpf(),
                request.getPhoneNumber(),
                request.getEstablishmentDocument(),
                request.getRequestedAmount(),
                request.getName(),
                request.getEmail(),
                request.getDateOfBirth(),
                toWire(request.getAddress()));
    }
}
