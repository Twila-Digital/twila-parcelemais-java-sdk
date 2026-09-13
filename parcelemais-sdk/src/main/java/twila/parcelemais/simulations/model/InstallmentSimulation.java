package twila.parcelemais.simulations.model;

import java.math.BigDecimal;
import lombok.Value;

@Value
public class InstallmentSimulation {
    BigDecimal totalAmount;
    int term;
    BigDecimal installmentAmount;
}
