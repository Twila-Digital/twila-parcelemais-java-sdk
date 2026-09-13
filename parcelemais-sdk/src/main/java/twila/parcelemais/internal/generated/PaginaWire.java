package twila.parcelemais.internal.generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PaginaWire {

    @JsonProperty("tem_proximo")
    public final boolean temProximo;

    @JsonProperty("tem_anterior")
    public final boolean temAnterior;

    @JsonProperty("numero")
    public final int numero;

    @JsonProperty("tamanho")
    public final int tamanho;

    @JsonProperty("total")
    public final int total;

    @JsonCreator
    public PaginaWire(
            @JsonProperty("tem_proximo") boolean temProximo,
            @JsonProperty("tem_anterior") boolean temAnterior,
            @JsonProperty("numero") int numero,
            @JsonProperty("tamanho") int tamanho,
            @JsonProperty("total") int total) {
        this.temProximo = temProximo;
        this.temAnterior = temAnterior;
        this.numero = numero;
        this.tamanho = tamanho;
        this.total = total;
    }
}
