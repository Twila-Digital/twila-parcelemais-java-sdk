package twila.parcelemais.establishments.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import twila.parcelemais.serialization.EnumMapping;
import twila.parcelemais.serialization.WireEnum;

public enum BankAccountType implements WireEnum {
    CURRENT(1),
    SAVINGS(2),
    PAYMENT(3),
    UNKNOWN(-1);

    private final int value;

    BankAccountType(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public int wireValue() {
        return value;
    }

    @JsonCreator
    public static BankAccountType fromWireValue(int value) {
        return EnumMapping.fromWireValue(BankAccountType.class, value);
    }
}
