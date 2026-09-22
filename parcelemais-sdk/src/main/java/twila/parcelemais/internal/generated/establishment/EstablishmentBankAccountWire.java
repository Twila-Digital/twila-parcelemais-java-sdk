package twila.parcelemais.internal.generated.establishment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EstablishmentBankAccountWire {

    @JsonProperty("banco")
    public final String banco;

    @JsonProperty("agencia")
    public final String agencia;

    @JsonProperty("digitoAgencia")
    public final String digitoAgencia;

    @JsonProperty("conta")
    public final String conta;

    @JsonProperty("digitoConta")
    public final String digitoConta;

    @JsonProperty("tipoConta")
    public final Integer tipoConta;

    @JsonProperty("nomeTitular")
    public final String nomeTitular;

    @JsonProperty("documentoTitular")
    public final String documentoTitular;

    @JsonCreator
    public EstablishmentBankAccountWire(
            @JsonProperty("banco") String banco,
            @JsonProperty("agencia") String agencia,
            @JsonProperty("digitoAgencia") String digitoAgencia,
            @JsonProperty("conta") String conta,
            @JsonProperty("digitoConta") String digitoConta,
            @JsonProperty("tipoConta") Integer tipoConta,
            @JsonProperty("nomeTitular") String nomeTitular,
            @JsonProperty("documentoTitular") String documentoTitular) {
        this.banco = banco;
        this.agencia = agencia;
        this.digitoAgencia = digitoAgencia;
        this.conta = conta;
        this.digitoConta = digitoConta;
        this.tipoConta = tipoConta;
        this.nomeTitular = nomeTitular;
        this.documentoTitular = documentoTitular;
    }
}
