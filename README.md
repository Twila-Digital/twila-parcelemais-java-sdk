<p align="center">
  <img src="https://raw.githubusercontent.com/Twila-Digital/twila-parcelemais-java-sdk/production/assets/logo-light.svg" alt="Parcele+" width="180" style="max-width: 100%;">
</p>

<p align="center">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/github/license/Twila-Digital/twila-parcelemais-java-sdk"></a>
  <a href="https://github.com/Twila-Digital/twila-parcelemais-java-sdk/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/Twila-Digital/twila-parcelemais-java-sdk/actions/workflows/ci.yml/badge.svg"></a>
  <a href="https://github.com/Twila-Digital/twila-parcelemais-java-sdk/actions/workflows/quality.yml"><img alt="Quality" src="https://github.com/Twila-Digital/twila-parcelemais-java-sdk/actions/workflows/quality.yml/badge.svg"></a>
  <a href="https://github.com/Twila-Digital/twila-parcelemais-java-sdk/security/code-scanning"><img alt="Security" src="https://github.com/Twila-Digital/twila-parcelemais-java-sdk/actions/workflows/security.yml/badge.svg"></a>
  <a href="https://codecov.io/gh/Twila-Digital/twila-parcelemais-java-sdk"><img alt="Coverage" src="https://codecov.io/gh/Twila-Digital/twila-parcelemais-java-sdk/branch/production/graph/badge.svg"></a>
  <img alt="Java" src="https://img.shields.io/badge/Java-8%2B-512BD4">
</p>

# ParceleMais (Java SDK)

SDK oficial em Java para a API do [Parcele+](https://www.cartaosimples.com.br) — crédito direto ao consumidor (CDC) e parcelamento no momento da compra.

> Uso restrito a server-side. O `clientSecret` nunca deve ser embarcado em um app Android, applet ou qualquer código que rode no dispositivo do usuário final.

## Compatibilidade

| Runtime | Versões aceitas |
| --- | --- |
| JDK | 8 ou superior (compilado com `--release 8`; testado em CI nas versões 8 e 21) |

HTTP via [OkHttp](https://square.github.io/okhttp/) — `java.net.http.HttpClient` do próprio JDK exigiria Java 11+, incompatível com o piso de Java 8 do SDK.

## Instalação

Maven:

```xml
<dependency>
    <groupId>br.com.twila</groupId>
    <artifactId>parcelemais</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
implementation("br.com.twila:parcelemais:1.0.0")
```

## Quick start

```java
import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.config.ParceleMaisEnvironment;

try (ParceleMaisClient client = ParceleMaisClient.builder()
        .clientId("<client-id>")
        .clientSecret("<client-secret>")
        .environment(ParceleMaisEnvironment.STAGING)
        .build()) {
    // client.orders(), client.simulations(), client.customers(), client.webhooks()
}
```

`ParceleMaisClient` é thread-safe e deve ser reaproveitado como singleton na sua aplicação (ele mantém o pool de conexões HTTP, o cache do token de acesso e o estado do circuit breaker) — feche-o só no shutdown, daí o `try-with-resources` acima ser normalmente o `main`/bootstrap da aplicação, não cada chamada.

### Simulando parcelas

```java
import twila.parcelemais.simulations.model.SimulateInstallmentsRequest;

List<InstallmentSimulation> parcelas = client.simulations().simulateInstallments(
        SimulateInstallmentsRequest.builder()
                .requestedAmount(new BigDecimal("1500.00"))
                .build());

for (InstallmentSimulation parcela : parcelas)
    System.out.printf("%dx de %s (total %s)%n", parcela.getTerm(), parcela.getInstallmentAmount(), parcela.getTotalAmount());
```

### Criando um pedido

```java
import twila.parcelemais.orders.model.Address;
import twila.parcelemais.orders.model.CreateOrderRequest;

UUID pedidoId = client.orders().create(CreateOrderRequest.builder()
        .cpf("12345678901")
        .phoneNumber("+5511999998888")
        .establishmentDocument("12345678000195")
        .requestedAmount(new BigDecimal("1500.00"))
        .name("Maria Souza")
        .email("maria.souza@exemplo.com.br")
        .dateOfBirth(OffsetDateTime.of(1990, 5, 20, 0, 0, 0, 0, ZoneOffset.of("-03:00")))
        .address(Address.builder()
                .street("Av. Paulista")
                .number("1578")
                .neighborhood("Bela Vista")
                .city("São Paulo")
                .state("SP")
                .postalCode("01311000")
                .build())
        .build());
```

`create` retorna só o `UUID` do pedido — a API não devolve o pedido completo na criação; use `client.orders().get(pedidoId)` se precisar dos dados completos logo em seguida.

## Clientes por recurso

| Cliente | Métodos |
| --- | --- |
| `client.orders()` | `create`, `get`, `list`, `startCdcSale`, `importInvoice` |
| `client.simulations()` | `simulateInstallments`, `simulateValues` |
| `client.customers()` | `get`, `list` |
| `client.establishments()` | `create`, `get`, `list`, `update`, `updateBankAccount`, `activate`, `deactivate` |
| `client.webhooks()` | `create`, `list`, `update`, `delete`, `listAudit` |

## Paginação

`orders().list(...)`, `customers().list(...)` e `webhooks().listAudit(...)` retornam `PagedResult<T>` — sem auto-paginação; você controla explicitamente o avanço de página:

```java
PagedResult<Order> page = client.orders().list(ListOrdersRequest.builder().page(1).pageSize(20).build());

for (Order order : page.getItems())
    System.out.println(order.getId());

if (page.isHasNext()) {
    PagedResult<Order> next = client.orders().list(ListOrdersRequest.builder().page(2).pageSize(20).build());
}
```

## Tratamento de erros

| Exceção | Quando |
| --- | --- |
| `ParceleMaisConfigurationException` | Configuração do `ParceleMaisClient.Builder` inválida (ex: `clientId`/`clientSecret` ausentes) |
| `ParceleMaisAuthenticationException` | Falha ao gerar/renovar o token de acesso |
| `ParceleMaisValidationException` | `400` — erro de validação, com `getErrors()` por campo |
| `ParceleMaisRateLimitException` | `429` |
| `ParceleMaisTimeoutException` | Timeout de rede, timeout total, ou circuit breaker aberto |
| `ParceleMaisApiException` | Qualquer outro erro de API (`404`, `409`, `5xx`) |
| `ParceleMaisWebhookSignatureException` | Assinatura de webhook inválida ou expirada |

```java
catch (ParceleMaisApiException ex) {
    System.out.printf("%d %s: %s%n", ex.getStatusCode(), ex.getErrorCode(), ex.getMessage());
}
```

## Validando webhooks

```java
import twila.parcelemais.webhooks.ParceleMaisWebhookEvent;
import twila.parcelemais.webhooks.model.OrderWebhookEvent;

OrderWebhookEvent evento = ParceleMaisWebhookEvent.parse(rawJson, signatureHeader, signingSecret);
```

Verifica a assinatura HMAC-SHA256 do cabeçalho e a janela de replay (5 minutos) antes de expor o evento. Lança `ParceleMaisWebhookSignatureException` se a assinatura for inválida ou o evento estiver fora da janela.

## Samples

- `samples/parcelemais-sample-plain` — Java puro, sem framework, `main()` standalone
- `samples/parcelemais-sample-spring-boot` — Spring Boot, `ParceleMaisClient` como `@Bean` singleton

## Qualidade, segurança e cobertura

- **Build** (`ci.yml`) — compila com `--release 8` em `ubuntu-latest` e `windows-latest`.
- **Test** (`ci.yml`) — testes unitários rodando sob JDK 8 (mínimo suportado) e JDK 21 (LTS atual).
- **Quality** (`quality.yml`) — análise estática via Codacy CLI, resultados publicados na aba **Security → Code scanning** do repositório.
- **Security** (`security.yml`) — [CodeQL](https://codeql.github.com/) para Java, rodando a cada PR/push e semanalmente.
- **Coverage** — cobertura de testes coletada via JaCoCo e publicada no [Codecov](https://codecov.io/gh/Twila-Digital/twila-parcelemais-java-sdk).

## Documentação completa

[documentacao.parcelemais.com.br](https://documentacao.parcelemais.com.br) — referência de todos os endpoints, autenticação, webhooks e mais.

## Contribuindo

Veja [CONTRIBUTING.md](CONTRIBUTING.md).

## Código de conduta

Este projeto segue o [Código de Conduta](CODE_OF_CONDUCT.md).

## Licença

[MIT](LICENSE)
