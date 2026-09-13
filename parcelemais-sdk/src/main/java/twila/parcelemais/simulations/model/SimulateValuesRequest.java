package twila.parcelemais.simulations.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SimulateValuesRequest {
    BigDecimal amount;
    int term;

    @Builder.Default
    CalculationValueType calculationValueType = CalculationValueType.GROSS_AMOUNT;
}
