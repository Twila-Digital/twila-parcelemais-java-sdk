package twila.parcelemais.webhooks.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import twila.parcelemais.serialization.EnumMapping;
import twila.parcelemais.serialization.WireEnum;

public enum WebHookType implements WireEnum {
    CUSTOMER(1),
    SIMULATION(2),
    ORDER(3),
    UNKNOWN(-1);

    private final int value;

    WebHookType(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public int wireValue() {
        return value;
    }

    @JsonCreator
    public static WebHookType fromWireValue(int value) {
        return EnumMapping.fromWireValue(WebHookType.class, value);
    }
}
