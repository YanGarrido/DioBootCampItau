# Budgeting API — Spring AI

Projeto final do módulo de Spring AI da DIO. A aplicação registra e consulta
transações financeiras e também permite executar comandos por áudio: o áudio é
transcrito, a IA escolhe uma ferramenta de negócio e a resposta final é convertida
novamente para áudio.

## O que o projeto faz

A API permite:

- registrar transações financeiras;
- listar transações por categoria;
- listar todas as transações;
- pesquisar transações pela descrição;
- consultar um resumo financeiro com quantidade, total e média;
- registrar e consultar transações usando comandos de voz com Spring AI.

Os valores são recebidos e armazenados em centavos. As categorias disponíveis são
`GROCERIES`, `PHARMA` e `AUTO`.

### Endpoints principais

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/transactions` | Registra uma transação. |
| `GET` | `/transactions` | Lista todas as transações. |
| `GET` | `/transactions/{category}` | Lista transações de uma categoria. |
| `GET` | `/transactions/search?description=mercado` | Pesquisa pela descrição. |
| `GET` | `/transactions/summary` | Retorna o resumo financeiro. |
| `POST` | `/transactions/ai` | Recebe áudio e devolve a resposta em MP3. |

## Como executar a aplicação

### Pré-requisitos

- Java 25;
- Docker e Docker Compose, para executar o MySQL;
- uma chave da API da OpenAI para os recursos de IA.

Configure a chave da OpenAI:

```bash
export OPENAI_API_KEY="sua-chave-aqui"
```

No Windows PowerShell:

```powershell
$env:OPENAI_API_KEY = "sua-chave-aqui"
```

Suba o banco de dados definido em `compose.yml` e execute a aplicação:

```bash
docker compose up -d
./gradlew bootRun
```

No Windows, use `gradlew.bat bootRun` no lugar de `./gradlew bootRun`.

A aplicação ficará disponível em `http://localhost:8080`.

## Qual melhoria foi implementada

Além da consulta original por categoria, foram implementados três novos tipos de
consulta financeira:

1. **Listagem geral** — retorna todas as transações registradas.
2. **Busca textual** — procura transações cuja descrição contenha o texto informado,
   sem diferenciar maiúsculas e minúsculas.
3. **Resumo financeiro** — calcula a quantidade de transações, o total em centavos e
   em reais e a média dos valores.

Essas funcionalidades foram implementadas como casos de uso independentes, expostas
por endpoints REST e registradas como ferramentas (`@Tool`) no `ChatClient`, para que
a IA também possa selecioná-las durante uma conversa.

## Tecnologias utilizadas

- Java 25;
- Spring Boot 4.0.5;
- Spring AI 2.0.0-M4;
- Spring Web, para os endpoints REST;
- Spring Data JPA e Hibernate, para persistência;
- MySQL, executado via Docker Compose;
- OpenAI, para transcrição, chat/tool calling e texto para áudio;
- Gradle, para build e execução dos testes;
- JUnit 5 e AssertJ, para testes automatizados;
- Lombok, para reduzir código repetitivo do domínio e das entidades.

## Como testar o fluxo principal

### 1. Registrar uma transação

Com a aplicação em execução:

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"description":"Compras do mês","category":"GROCERIES","amount":15000}'
```

O campo `amount` representa centavos; nesse exemplo, o valor é R$ 150,00.

### 2. Consultar as transações

```bash
curl http://localhost:8080/transactions
curl http://localhost:8080/transactions/GROCERIES
curl "http://localhost:8080/transactions/search?description=compras"
curl http://localhost:8080/transactions/summary
```

### 3. Testar o fluxo de voz com IA

Envie um arquivo de áudio contendo, por exemplo, “gastei 80 reais no mercado”:

```bash
curl -X POST http://localhost:8080/transactions/ai \
  -F "file=@caminho/para/audio.m4a" \
  --output resposta.mp3
```

Nesse fluxo, o áudio é transcrito pelo modelo de transcrição, o `ChatClient` usa as
ferramentas de persistência ou consulta e o resultado é convertido para MP3 pelo
modelo de texto para fala.

## Testes automatizados

Para executar os testes:

```bash
./gradlew test
```

Os testes unitários das novas consultas podem ser executados isoladamente, sem
OpenAI ou banco de dados:

```bash
./gradlew test --tests dio.budgeting.application.TransactionQueryUseCasesTest
```

Os testes de integração com OpenAI só são executados quando `OPENAI_API_KEY` está
configurada. O teste de contexto da aplicação precisa de um MySQL disponível.

## O que foi aprendido durante o desafio

- como integrar transcrição de áudio, chat com tool calling e texto para fala em um
  fluxo único;
- como manter a IA na camada de infraestrutura sem permitir que ela contorne os
  casos de uso e as regras do domínio;
- como aplicar Repository Pattern e uma arquitetura em camadas em uma API Spring;
- como representar valores monetários em centavos para evitar problemas de precisão;
- como transformar o mesmo caso de uso em uma ferramenta para a IA e em um endpoint
  REST;
- como criar testes unitários independentes de serviços externos.

## Estrutura do projeto

- `src/main/java/dio/budgeting/domain` — entidades, categorias e contrato do
  repositório;
- `src/main/java/dio/budgeting/application` — casos de uso e objetos de entrada e
  saída;
- `src/main/java/dio/budgeting/infrastructure` — controladores HTTP, JPA e integração
  com Spring AI;
- `src/test` — testes unitários e testes de integração com os modelos da OpenAI.

## Referências do Spring AI

- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html)
- [ChatModel API](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Tools API](https://docs.spring.io/spring-ai/reference/api/tools.html)
- [Audio Transcriptions API](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
- [Audio Speech API](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)
