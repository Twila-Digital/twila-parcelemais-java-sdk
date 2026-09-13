package twila.parcelemais.internal.generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PagedResultWire<T> {

    @JsonProperty("itens")
    public final List<T> itens;

    @JsonProperty("pagina")
    public final PaginaWire pagina;

    @JsonCreator
    public PagedResultWire(@JsonProperty("itens") List<T> itens, @JsonProperty("pagina") PaginaWire pagina) {
        this.itens = itens;
        this.pagina = pagina;
    }
}
