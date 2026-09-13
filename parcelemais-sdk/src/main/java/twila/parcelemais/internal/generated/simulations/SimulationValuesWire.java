package twila.parcelemais.internal.generated.simulations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public final class SimulationValuesWire {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class EstablishmentSimulationValuesWire {

        @JsonProperty("valorVenda")
        public final BigDecimal valorVenda;

        @JsonProperty("valorDesembolso")
        public final BigDecimal valorDesembolso;

        @JsonCreator
        public EstablishmentSimulationValuesWire(
                @JsonProperty("valorVenda") BigDecimal valorVenda,
                @JsonProperty("valorDesembolso") BigDecimal valorDesembolso) {
            this.valorVenda = valorVenda;
            this.valorDesembolso = valorDesembolso;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class CustomerSimulationValuesWire {

        @JsonProperty("valorParcela")
        public final BigDecimal valorParcela;

        @JsonCreator
        public CustomerSimulationValuesWire(@JsonProperty("valorParcela") BigDecimal valorParcela) {
            this.valorParcela = valorParcela;
        }
    }

    @JsonProperty("valoresEstabelecimento")
    public final EstablishmentSimulationValuesWire valoresEstabelecimento;

    @JsonProperty("valoresCliente")
    public final CustomerSimulationValuesWire valoresCliente;

    @JsonCreator
    public SimulationValuesWire(
            @JsonProperty("valoresEstabelecimento") EstablishmentSimulationValuesWire valoresEstabelecimento,
            @JsonProperty("valoresCliente") CustomerSimulationValuesWire valoresCliente) {
        this.valoresEstabelecimento = valoresEstabelecimento;
        this.valoresCliente = valoresCliente;
    }
}
