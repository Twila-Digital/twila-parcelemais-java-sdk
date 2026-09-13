package twila.parcelemais.internal.generated.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public final class ImportOrderInvoiceRequestWire {

    @JsonProperty("pedidoId")
    public final UUID pedidoId;

    @JsonProperty("arquivoBase64")
    public final String arquivoBase64;

    @JsonProperty("nomeArquivo")
    public final String nomeArquivo;

    public ImportOrderInvoiceRequestWire(UUID pedidoId, String arquivoBase64, String nomeArquivo) {
        this.pedidoId = pedidoId;
        this.arquivoBase64 = arquivoBase64;
        this.nomeArquivo = nomeArquivo;
    }
}
