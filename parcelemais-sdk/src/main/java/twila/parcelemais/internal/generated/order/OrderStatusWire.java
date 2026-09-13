package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderStatusWire {

    @JsonProperty("valor")
    public final int valor;

    @JsonProperty("descricao")
    public final String descricao;

    @JsonCreator
    public OrderStatusWire(@JsonProperty("valor") int valor, @JsonProperty("descricao") String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }
}
