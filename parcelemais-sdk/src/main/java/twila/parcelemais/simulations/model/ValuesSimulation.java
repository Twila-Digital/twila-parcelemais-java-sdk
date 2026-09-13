package twila.parcelemais.simulations.model;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class ValuesSimulation {
    BigDecimal saleAmount;
    BigDecimal disbursementAmount;
    BigDecimal installmentAmount;
}
