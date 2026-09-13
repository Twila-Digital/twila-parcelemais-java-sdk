package twila.parcelemais.serialization;

public final class EnumMapping {

    private EnumMapping() {
    }

    public static <E extends Enum<E> & WireEnum> E fromWireValue(Class<E> enumType, int value) {
        for (E candidate : enumType.getEnumConstants()) {
            if (candidate.wireValue() == value)
                return candidate;
        }

        return Enum.valueOf(enumType, "UNKNOWN");
    }
}
