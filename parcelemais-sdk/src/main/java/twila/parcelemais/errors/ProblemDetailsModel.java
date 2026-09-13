package twila.parcelemais.errors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * Modelo do {@code ProblemDetails} (campos em português) devolvido pela API em respostas de erro.
 * Campos ausentes/nulos/desconhecidos nunca causam falha de desserialização.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = false)
public class ProblemDetailsModel {

    @JsonProperty("tipo")
    private final String type;

    @JsonProperty("titulo")
    private final String title;

    @JsonProperty("status")
    private final Integer status;

    @JsonProperty("detalhe")
    private final String detail;

    @JsonProperty("instancia")
    private final String instance;

    @JsonProperty("erros")
    private final Map<String, String[]> errors;

    @JsonProperty("correlationId")
    private final String correlationId;

    /**
     * Campos não mapeados nas propriedades acima.
     */
    private final Map<String, Object> extensionData = new HashMap<>();

    @JsonCreator
    public ProblemDetailsModel(
            @JsonProperty("tipo") String type,
            @JsonProperty("titulo") String title,
            @JsonProperty("status") Integer status,
            @JsonProperty("detalhe") String detail,
            @JsonProperty("instancia") String instance,
            @JsonProperty("erros") Map<String, String[]> errors,
            @JsonProperty("correlationId") String correlationId) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.errors = errors;
        this.correlationId = correlationId;
    }

    @JsonAnySetter
    void setExtension(String name, Object value) {
        extensionData.put(name, value);
    }

    @JsonAnyGetter
    Map<String, Object> getExtensionData() {
        return extensionData;
    }

    public static ProblemDetailsModel empty() {
        return empty(null);
    }

    public static ProblemDetailsModel empty(String fallbackDetail) {
        return new ProblemDetailsModel(null, null, null, fallbackDetail, null, null, null);
    }
}
