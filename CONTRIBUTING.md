# Contribuindo

## Pré-requisitos

- JDK 8 ou superior (o build compila com `--release 8`; JDK mais novo funciona normalmente)
- Maven 3.9+
- (Opcional) Credenciais de staging para rodar os samples/testes de integração manual

## Build e testes

```bash
mvn -pl parcelemais-sdk -am install
mvn -pl parcelemais-sdk test
```

Rode os testes em **todas** as versões de JDK que o pacote promete suportar antes de abrir o PR — não só a mais recente. Veja `SDK-PLAYBOOK.md` §2.4/§1.5 no repositório `sdks-twila`.

Os contract tests (`contract-tests/`) fazem uma chamada real ao OpenAPI de staging e não rodam por padrão — veja o workflow `contract-tests.yml`.

## Instalando a partir do código-fonte

Enquanto o pacote não é publicado no Maven Central, instale no repositório Maven local:

```bash
mvn -pl parcelemais-sdk -am install -DskipTests
```

E declare a dependência normalmente no seu `pom.xml`/`build.gradle.kts` apontando para a mesma versão (`1.0.0-SNAPSHOT`).

## Abrindo um PR

1. Crie uma branch a partir de `production`
2. Adicione testes para qualquer mudança de comportamento
3. Rode `mvn -pl parcelemais-sdk -am test` localmente antes de abrir o PR
4. Abra o PR contra `production` — o CI roda build + testes automaticamente

## Release (publicação no Maven Central)

Maven Central (via [Sonatype Central Portal](https://central.sonatype.com/)) **ainda não oferece Trusted Publishing/OIDC** por execução de workflow, ao contrário do NuGet.org/npm/PyPI/RubyGems/crates.io (ver `SDK-PLAYBOOK.md` §1.6 no repositório `sdks-twila`) — a autenticação de publish usa um Sonatype User Token tradicional, e todo artefato precisa ser assinado com GPG. `release.yml` usa o fallback documentado no playbook: um secret de escopo mínimo guardado no `environment` do GitHub, nunca no repositório inteiro.

Antes do primeiro release, alguém com acesso à conta/organização do Sonatype Central Portal precisa configurar:

1. Criar o namespace `br.com.twila` no [Central Portal](https://central.sonatype.com/) — reverse-DNS do domínio `twila.com.br`, verificado via registro TXT no DNS desse domínio.
2. Gerar um **User Token** (Account → Generate User Token) com permissão apenas de publish.
3. Gerar (ou reaproveitar) uma chave GPG usada só para assinar releases deste SDK, e publicá-la num keyserver (`keys.openpgp.org` ou `keyserver.ubuntu.com`).
4. No repositório do GitHub, criar o [environment](https://docs.github.com/actions/deployment/targeting-different-environments/using-environments-for-deployment) `production` (Settings → Environments) com **Required reviewers** configurado, e os secrets:
   - `CENTRAL_TOKEN_USERNAME` / `CENTRAL_TOKEN_PASSWORD` — o User Token do Central Portal.
   - `GPG_PRIVATE_KEY` — a chave privada exportada em ASCII-armor (`gpg --export-secret-keys --armor`).
   - `GPG_PASSPHRASE` — a senha da chave.

Com isso configurado, `git push --tags` numa tag `v*` (ex.: `v1.0.0`) dispara build → contract tests → assinatura → publicação no Central Portal (como *deployment* pendente de release manual no painel, ou automático se `autoPublish` for habilitado depois que o processo estiver validado).

## Reportando problemas

Abra uma [issue](https://github.com/Twila-Digital/twila-parcelemais-java-sdk/issues) com passos para reproduzir, versão do pacote/JDK e o comportamento esperado vs. observado. Nunca inclua `clientId`/`clientSecret` reais no relato.
