package twila.parcelemais.webhooks.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import twila.parcelemais.serialization.EnumMapping;
import twila.parcelemais.serialization.WireEnum;

public enum WebHookAuthenticationType implements WireEnum {
    NONE(1),
    BASIC(2),
    JWT(3),
    UNKNOWN(-1);

    private final int value;

    WebHookAuthenticationType(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public int wireValue() {
        return value;
    }

    @JsonCreator
    public static WebHookAuthenticationType fromWireValue(int value) {
        return EnumMapping.fromWireValue(WebHookAuthenticationType.class, value);
    }
}
