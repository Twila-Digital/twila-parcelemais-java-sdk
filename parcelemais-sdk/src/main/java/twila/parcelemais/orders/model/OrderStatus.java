package twila.parcelemais.orders.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import twila.parcelemais.serialization.EnumMapping;
import twila.parcelemais.serialization.WireEnum;

public enum OrderStatus implements WireEnum {
    UNDEFINED(0),
    ANALYSING(1),
    APPROVED(2),
    UNAVAILABLE_BALANCE(3),
    ANALYSIS_EXPIRED(4),
    PENDING_PAYMENT(5),
    BIOMETRY_REFUSED(6),
    BIOMETRY_APPROVED(7),
    PAYMENT_REFUSED(8),
    PURCHASED(9),
    UNAUTHORIZED(10),
    PENDING_AUTHORIZATION(11),
    AWAITING_REGISTRATION(12),
    SALE_NOT_STARTED(13),
    CANCELED(14),
    BILLING(15),
    COMPLETED(16),
    FROZEN(17),
    PENDING_PAYMENT_CONFIRMATION(18),
    DISBURSED(19),
    UNKNOWN(-1);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public int wireValue() {
        return value;
    }

    @JsonCreator
    public static OrderStatus fromWireValue(int value) {
        return EnumMapping.fromWireValue(OrderStatus.class, value);
    }
}
