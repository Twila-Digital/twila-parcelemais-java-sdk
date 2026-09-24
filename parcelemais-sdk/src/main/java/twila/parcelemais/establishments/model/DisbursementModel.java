package twila.parcelemais.establishments.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import twila.parcelemais.serialization.EnumMapping;
import twila.parcelemais.serialization.WireEnum;

public enum DisbursementModel implements WireEnum {
    ESTABLISHMENT_CHAIN(1),
    ESTABLISHMENT(2),
    EXTERNAL(3),
    UNKNOWN(-1);

    private final int value;

    DisbursementModel(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public int wireValue() {
        return value;
    }

    @JsonCreator
    public static DisbursementModel fromWireValue(int value) {
        return EnumMapping.fromWireValue(DisbursementModel.class, value);
    }
}
