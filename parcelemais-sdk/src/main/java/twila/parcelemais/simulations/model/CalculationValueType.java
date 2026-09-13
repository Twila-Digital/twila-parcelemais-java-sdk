package twila.parcelemais.simulations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import twila.parcelemais.serialization.EnumMapping;
import twila.parcelemais.serialization.WireEnum;

public enum CalculationValueType implements WireEnum {
    GROSS_AMOUNT(1),
    LIQUID_AMOUNT(2),
    UNKNOWN(-1);

    private final int value;

    CalculationValueType(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public int wireValue() {
        return value;
    }

    @JsonCreator
    public static CalculationValueType fromWireValue(int value) {
        return EnumMapping.fromWireValue(CalculationValueType.class, value);
    }
}
