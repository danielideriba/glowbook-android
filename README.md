# Descrição do projeto GlowBook

O GlowBook é um aplicativo Android de agendamento para salões e serviços de beleza.
Na tela inicial, o cliente vê seu próximo agendamento, as categorias de serviço (cabelo, unhas, estética, spa) e os profissionais disponíveis, com especialidade e avaliação.
Ao escolher um profissional, o cliente seleciona um ou vários serviços, como corte, escova ou coloração, e o app calcula na hora o valor total e o tempo estimado do atendimento.
Em seguida, ele escolhe o dia e o horário numa agenda dos próximos dias; horários já ocupados ou que já passaram aparecem bloqueados.
Antes de confirmar, um resumo mostra profissional, serviços, data, hora e duração prevista.
Ao confirmar, o agendamento é registrado diretamente no sistema do salão.
Na área "Meus agendamentos", o cliente acompanha seus próximos horários, o histórico e os cancelados, podendo ver detalhes, reagendar, cancelar ou avaliar o atendimento.
O objetivo é tirar o agendamento do telefone e do WhatsApp: o cliente marca sozinho, a qualquer hora, e o salão recebe tudo organizado, sem conflitos de horário.

## Solução proposta

Hoje, boa parte dos salões de beleza ainda faz os agendamentos por telefone, WhatsApp ou caderno. Isso gera mensagens perdidas, horários marcados em dobro, clientes esperando resposta e profissionais interrompidos durante o atendimento para conferir a agenda.

O GlowBook propõe o **autoatendimento**: o próprio cliente monta o agendamento em poucos toques, a qualquer hora do dia, sem depender de alguém do salão para responder.

Os pilares da solução são:

- **Disponibilidade sempre atualizada**: a agenda consulta os agendamentos já existentes de cada profissional e bloqueia automaticamente os horários ocupados ou que já passaram, evitando conflitos.
- **Transparência de preço e tempo**: ao escolher os serviços, o cliente vê na hora o valor total e a duração estimada, antes de confirmar qualquer coisa.
- **Vários serviços em um só agendamento**: corte, escova e coloração, por exemplo, são reservados juntos com o mesmo profissional, em um único horário.
- **Confirmação imediata**: ao confirmar, o agendamento é gravado direto na base do salão, sem etapa manual de aprovação.
- **Acompanhamento pelo cliente**: próximos horários, histórico e cancelados ficam reunidos em "Meus agendamentos", com atalhos para reagendar, cancelar ou avaliar.

Para o salão, o resultado é uma agenda organizada e centralizada, menos tempo gasto respondendo mensagens e menos faltas e conflitos de horário. Para o cliente, é a praticidade de agendar sozinho, com clareza sobre o que vai ser feito, quanto vai custar e quanto tempo vai levar.

## O que ficou incompleto e melhoraria com mais tempo?

A base do projeto facilita a evolução do produto, mas alguns pontos ficaram de fora nesta versão:

**Funcionalidades**

- **Login e perfil do cliente**: o nome exibido é fixo ("Usuário") e os agendamentos não estão ligados a um cliente; com login, cada pessoa veria apenas os seus horários.
- **Ações de "Meus agendamentos"**: os botões Ver detalhes, Reagendar, Cancelar e Avaliar já aparecem na tela, mas ainda não executam nenhuma ação.
- **Busca e Perfil**: os itens "Buscar" e "Perfil" da barra inferior ainda não levam a nenhuma tela.
- **Filtro por categoria**: selecionar uma categoria na tela inicial ainda não filtra os profissionais.
- **Tela de confirmação**: após agendar, o app mostra um aviso e abre "Meus agendamentos"; uma tela própria de confirmação, como no protótipo, deixaria o fluxo mais claro.
- **Pagamentos e lembretes**: pagamento antecipado pelo app e notificações de lembrete antes do horário reduziriam faltas.

**Regras de negócio**

- **Serviços por profissional**: a base não relaciona profissionais aos serviços que cada um realiza, então todos os serviços aparecem para qualquer profissional.
- **Expediente de cada profissional**: os horários são fixos (das 8h às 11h e das 14h às 18h) e não existe cadastro de folgas ou jornada por profissional.
- **Duração real do atendimento**: cada horário ocupa 1 hora, independentemente da duração dos serviços escolhidos; um atendimento de 2h15min deveria bloquear os horários seguintes.

**Qualidade técnica**

- **Chaves fora do código**: a chave da API e o token do Supabase estão no código-fonte; o ideal é movê-los para configuração de build (`local.properties`/`BuildConfig`) e usar autenticação por usuário em vez do token anônimo.
- **Textos e mensagens**: alguns textos da interface ainda estão fixos no código, em vez de estarem no `strings.xml`, e uma mensagem de erro ainda aparece em inglês.
- **Robustez da camada de dados**: tratar o cancelamento de chamadas sem convertê-lo em erro e garantir valores padrão corretos quando a API omitir campos.

## Tech Stack utilizada

**Linguagem e plataforma**

- **Kotlin 2.4**: linguagem do projeto, com Coroutines e Flow para operações assíncronas.
- **Android SDK**: `minSdk 24`, `targetSdk 36`, `compileSdk 37`.
- **Java 11 + desugaring (`desugar_jdk_libs`)**: permite usar `java.time` (`LocalDate`, `LocalTime`) em versões antigas do Android.
- **Gradle 9.3 + Android Gradle Plugin 9.1**: build com Kotlin DSL e *version catalog* (`libs.versions.toml`).

**Interface**

- **Jetpack Compose (BOM 2026.08)**: UI declarativa, com cada bloco da tela como um composable próprio.
- **Material 3**: componentes e tema.
- **Navigation Compose**: navegação entre telas com rotas e argumentos (`AppScreen`, `AppNavHost`).
- **Lifecycle / ViewModel + StateFlow**: estado da tela sobrevive a rotações e é observado pelo Compose.
- **Activity Compose**: ponto de entrada (`MainActivity`) com *edge-to-edge*.

**Dados e rede**

- **Supabase (PostgREST)**: backend com banco Postgres exposto como API REST; filtros como `eq.`, `in.()`, `gte.`/`lte.` e função RPC `criar_agendamento`.
- **Retrofit 3**: cliente HTTP declarativo para as APIs.
- **OkHttp 5 + Logging Interceptor**: interceptors para `apikey` e `Authorization: Bearer`, cache em disco e timeouts.
- **Gson**: conversão JSON ↔ Kotlin com `@SerializedName`.

**Arquitetura**

- **Clean Architecture em camadas**: `ApiService → DataSource → Repository → UseCase → ViewModel → Screen`.
- **MVVM**: ViewModels expõem `StateFlow<UiState<T>>` (`Ready`, `Loading`, `Success`, `Error`) para as telas.
- **Repository pattern**: interfaces no `domain`, implementações no `data`, com `safeApiCall` padronizando sucesso e erro (`ApiState`).
- **Hilt (Dagger)**: injeção de dependências com `NetworkModule`, `RepositoryModule`, `AppModule` e qualificadores (`@CreateAppointmentClient`, `@BearerInterceptor`).
- **KSP**: processamento de anotações do Hilt.

**Testes e qualidade**

- **JUnit 4**: testes unitários e instrumentados.
- **MockK**: mocks em testes unitários e no Android.
- **kotlinx-coroutines-test**: `runTest`, dispatchers de teste para Flows e ViewModels.
- **Compose UI Test + Espresso**: testes de interface no emulador.
- **Hilt Testing**: `@HiltAndroidTest`, `@UninstallModules` e backend falso para testes de ponta a ponta.
- **JaCoCo**: relatório de cobertura (`./gradlew :app:jacocoTestReport`).

## Estrutura do projeto

**Fluxo das camadas**

```
┌──────────────────────────────────────────────────────────────┐
│  UI (Jetpack Compose)                                        │
│  Screen ──► Content (stateless) ──► Components               │
│     ▲                                                        │
│     │ StateFlow<UiState<T>>                                  │
│  ViewModel                                                   │
└─────┬────────────────────────────────────────────────────────┘
      │ chama
┌─────▼────────────────────────────────────────────────────────┐
│  DOMAIN                                                      │
│  UseCase ──► Repository (interface)       Models (Entity)    │
└─────┬────────────────────────────────────────────────────────┘
      │ implementado por
┌─────▼────────────────────────────────────────────────────────┐
│  DATA                                                        │
│  RepositoryImpl ──► DataSource ──► ApiService (Retrofit)     │
│        safeApiCall → ApiState         Models (Response)      │
└─────┬────────────────────────────────────────────────────────┘
      │ HTTPS (apikey / Bearer)
┌─────▼────────────────────────────────────────────────────────┐
│  SUPABASE  (PostgREST + Postgres + RPC criar_agendamento)    │
└──────────────────────────────────────────────────────────────┘

        DI (Hilt): NetworkModule · RepositoryModule · AppModule
```

**Pastas**

```
app/src/
├── main/java/com/glowbook/android/
│   ├── data/
│   │   ├── remote/            # ApiServices (Retrofit), ApiPath, ApiClientConstants
│   │   │   └── model/         # Responses e requests da API (Gson)
│   │   ├── datasource/        # Interfaces + Impl que chamam as ApiServices
│   │   └── repository/        # RepositoryImpl: combina data sources e trata erros
│   ├── domain/
│   │   ├── models/            # Entidades de negócio (ProfessionalEntity, ServiceEntity…)
│   │   ├── repository/        # Contratos dos repositórios
│   │   └── usecase/           # Regras de negócio: Response → Entity → UiState
│   ├── di/                    # Módulos Hilt e qualificadores
│   ├── ui/
│   │   ├── common/theme/      # Cores, tipografia e tema
│   │   ├── components/        # Componentes compartilhados (barra inferior, SectionLabel)
│   │   ├── features/
│   │   │   ├── home/          # Tela inicial
│   │   │   ├── professional/  # Escolha de serviços do profissional
│   │   │   ├── calendar/      # Escolha de data/horário e confirmação
│   │   │   └── appointments/  # Meus agendamentos
│   │   │       ├── components/   # (em cada feature) blocos visuais da tela
│   │   │       └── model/        # (em cada feature) UI models e formatação
│   │   ├── nav/               # Rotas (AppScreen) e AppNavHost
│   │   └── viewmodel/         # ViewModels das telas
│   └── utils/                 # UiState, ApiState/safeApiCall, dispatchers, locale
├── test/                      # Testes unitários (JVM), espelham a estrutura do main
└── androidTest/               # Testes de UI e de DI no emulador
    ├── di/                    # Grafo Hilt real e fluxo com backend falso
    ├── ui/                    # Telas, componentes e navegação
    └── testutils/             # FakeSupabase, FakeRetrofit
```

## Análise de escalabilidade

**Escalabilidade do código**

- **Camadas independentes**: como a UI só conhece use cases e o domínio só conhece interfaces, é possível trocar ou somar fontes de dados (por exemplo, um cache local com Room para uso offline) sem mexer nas telas.
- **Modularização**: a estrutura por `features/` já está pronta para virar módulos Gradle separados (`:feature:home`, `:feature:calendar`, `:core:network`, `:core:domain`), o que acelera o build e permite que times diferentes trabalhem em paralelo.
- **Novas funcionalidades**: login, pagamentos ou avaliações seguem o mesmo caminho (ApiService → DataSource → Repository → UseCase → ViewModel), registrando as dependências no Hilt.
- **Multiplataforma**: as camadas `domain` e `data` são Kotlin puro e podem ser migradas para Kotlin Multiplatform, reaproveitando a lógica em um app iOS.
- **Segurança nas mudanças**: a cobertura de testes unitários e de interface reduz o risco de regressões conforme o código cresce.

**Escalabilidade do negócio**

- **Vários salões (multi-tenant)**: adicionando um identificador de salão nas tabelas e nas consultas, o mesmo app atende várias empresas, podendo inclusive ter tema e marca próprios (white-label).
- **Painel do salão**: a mesma base no Supabase pode alimentar um painel web para o salão gerenciar profissionais, serviços, horários e agendamentos.
- **Expediente e duração reais**: cadastrar a jornada de cada profissional e considerar a duração dos serviços permite agendas mais precisas conforme o volume cresce.

**Escalabilidade do backend**

- **Supabase/Postgres**: o PostgREST escala horizontalmente e o Postgres suporta alto volume com índices adequados (por exemplo, em `agendamentos(profissional_id, data)`).
- **Concorrência de horários**: com muitos clientes simultâneos, a garantia contra dois agendamentos no mesmo horário deve ficar no banco (restrição ou validação transacional na função `criar_agendamento`), não apenas no app.
- **Menos requisições**: usar *views* ou *embedding* do PostgREST para trazer agendamentos já com seus serviços evita várias chamadas por tela.
- **Paginação**: histórico e listas grandes devem ser paginados (`limit`/`offset` ou `Range`), em vez de carregados por inteiro.
- **Tempo real**: o Supabase Realtime pode atualizar os horários disponíveis na tela assim que outro cliente reservar.
- **Segurança**: com autenticação de usuários e *Row Level Security*, cada cliente acessa apenas os próprios dados, o que é essencial ao crescer.

## Como rodar os testes

**Testes unitários** (JVM, não precisam de emulador):

```bash
./gradlew :app:testDebugUnitTest
```

Para rodar apenas uma classe de teste:

```bash
./gradlew :app:testDebugUnitTest --tests "com.glowbook.android.di.RepositoryModuleTest"
```

**Testes de interface e de DI** (precisam de um emulador ou aparelho conectado):

```bash
./gradlew :app:connectedDebugAndroidTest
```

Com mais de um dispositivo conectado, escolha onde rodar pelo `ANDROID_SERIAL` (veja os disponíveis com `adb devices`):

```bash
ANDROID_SERIAL=emulator-5554 ./gradlew :app:connectedDebugAndroidTest
```

Relatórios gerados:

- Unitários: `app/build/reports/tests/testDebugUnitTest/index.html`
- Interface: `app/build/reports/androidTests/connected/debug/index.html`

## Como obter o code coverage report do projeto?

O relatório de cobertura usa o JaCoCo e roda os testes unitários antes de gerá-lo:

```bash
./gradlew :app:jacocoTestReport
```

Relatórios gerados:

- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML (para ferramentas de CI): `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

Para abrir o relatório HTML no macOS:

```bash
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

> O relatório considera os testes unitários e exclui código gerado (Hilt, `R`, `BuildConfig`) e as pastas `ui/` e `di/`, cuja cobertura vem dos testes de interface no emulador.
