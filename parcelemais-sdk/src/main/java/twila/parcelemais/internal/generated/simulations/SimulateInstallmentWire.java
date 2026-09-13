package twila.parcelemais.internal.generated.simulations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SimulateInstallmentWire {

    @JsonProperty("valorTotalDebito")
    public final BigDecimal valorTotalDebito;

    @JsonProperty("prazo")
    public final int prazo;

    @JsonProperty("valorParcela")
    public final BigDecimal valorParcela;

    @JsonCreator
    public SimulateInstallmentWire(
            @JsonProperty("valorTotalDebito") BigDecimal valorTotalDebito,
            @JsonProperty("prazo") int prazo,
            @JsonProperty("valorParcela") BigDecimal valorParcela) {
        this.valorTotalDebito = valorTotalDebito;
        this.prazo = prazo;
        this.valorParcela = valorParcela;
    }
}
