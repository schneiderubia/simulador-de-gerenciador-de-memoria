# Simulador de Alocação Dinâmica de Memória

Trabalho prático de Sistemas Operacionais — simulador de alocação de memória com partições variáveis, suportando os algoritmos **First Fit**, **Best Fit** e **Worst Fit**.

O projeto oferece **duas formas de uso**:

1. **Modo texto (CLI) dirigido por arquivo**
2. **Interface gráfica (Compose Desktop)**

O núcleo da simulação (`Bloco.java`, `GerenciadorMemoria.java`) é escrito em
**Java puro** e é compartilhado pelos dois modos.

## Pré-requisitos

| Ferramenta          | Versão                      | Para quê                                                                                                               |
| ------------------- | --------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **JDK**             | **25** (toolchain do build) | Compilar e executar. O build está configurado para usar o JDK 25 como _toolchain_.                                     |
| **Gradle**          | 9.5.1                       | **Não precisa instalar.** O projeto traz o _Gradle Wrapper_ (`./gradlew`), que baixa a versão correta automaticamente. |
| **Kotlin**          | 2.2.20                      | **Não precisa instalar.** É resolvido pelo Gradle como dependência do build.                                           |
| **Compose Desktop** | 1.9.0                       | **Não precisa instalar.** Resolvido pelo Gradle.                                                                       |
| **Git**             | qualquer                    | Opcional, apenas para clonar o repositório.                                                                            |

> **A única coisa que você precisa instalar manualmente é um JDK.** Todo o resto
> (Gradle, Kotlin, Compose e suas bibliotecas) é baixado automaticamente pelo
> wrapper na primeira execução — basta ter conexão com a internet.

### Observação importante sobre a versão do JDK

O build usa **JDK 25 como _toolchain_** (ambiente que compila e empacota), mas o
**bytecode gerado tem alvo 24** (`jvmTarget = 24`), porque 24 é o maior alvo
suportado pelo compilador Kotlin atual (2.2.20). Java e Kotlin são mantidos no
mesmo alvo de propósito, para evitar erro de inconsistência do Gradle.

Na prática isso significa: **você precisa ter o JDK 25 instalado na máquina.** Se
tiver outra versão (24, 26…), veja a seção
[Solução de problemas](#solução-de-problemas-troubleshooting) — há duas saídas:
instalar o JDK 25 ou apontar o build para o JDK que você já tem.

---

## Instalação dos pré-requisitos (passo a passo)

### Linux (recomendado: SDKMAN)

O **SDKMAN** é a forma mais simples de instalar e alternar entre versões de JDK
no Linux, sem mexer no sistema.

**1. Instalar o SDKMAN** (se ainda não tiver):

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

**2. Listar os JDKs 25 disponíveis:**

```bash
sdk list java | grep '25'
```

**3. Instalar um JDK 25** (a distribuição Temurin/Eclipse é uma boa escolha; o
identificador exato — ex. `25-tem` — aparece na listagem do passo anterior):

```bash
sdk install java 25-tem
```

**4. Verificar:**

```bash
java -version
# deve mostrar "openjdk version 25..."
```

> Se você já tem **outros JDKs** instalados via SDKMAN, pode deixar o 25 como
> padrão da sessão com `sdk use java 25-tem`, ou como padrão global com
> `sdk default java 25-tem`.

### macOS

No macOS você também pode usar o **SDKMAN** (mesmos passos do Linux) ou o
**Homebrew**. Escolha **uma** das opções.

**Opção 1 — SDKMAN** (recomendada, permite ter várias versões lado a lado):

```bash
# 1. Instalar o SDKMAN (se ainda não tiver)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# 2. Listar os JDKs 25 disponíveis
sdk list java | grep '25'

# 3. Instalar um JDK 25 (o identificador exato aparece na listagem acima)
sdk install java 25-tem

# 4. Verificar
java -version
```

**Opção 2 — Homebrew:**

```bash
# 1. Instalar o JDK (a fórmula instala a versão mais recente do OpenJDK)
brew install openjdk

# 2. Linkar para o sistema enxergar (o brew mostra o comando exato no final;
#    em geral é algo assim:)
sudo ln -sfn $(brew --prefix)/opt/openjdk/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk.jdk

# 3. Verificar
java -version
```

> **Apple Silicon (M1/M2/M3…):** ambas as opções já trazem o JDK nativo para ARM.
> Nada de especial a fazer.

> Se o `brew install openjdk` instalar uma versão diferente da 25, prefira o
> SDKMAN (que deixa escolher a 25), ou use a
> [Solução B do troubleshooting](#solução-de-problemas-troubleshooting) para
> apontar o build ao JDK que ficou instalado.

### Windows

No Windows, a forma mais simples é o **winget** (gerenciador de pacotes nativo do
Windows 10/11). Alternativas: instalador `.msi` da Adoptium ou o **SDKMAN** rodando
sob WSL/Git Bash.

**Opção 1 — winget** (recomendada). Abra o **PowerShell** e rode:

```powershell
# 1. Procurar os JDKs 25 disponíveis
winget search Microsoft.OpenJDK

# 2. Instalar o JDK 25 (ex.: Microsoft Build of OpenJDK 25)
winget install Microsoft.OpenJDK.25

# 3. FECHE e reabra o PowerShell (para recarregar o PATH) e verifique
java -version
```

**Opção 2 — Instalador gráfico (Adoptium / Temurin):**

1. Acesse <https://adoptium.net/temurin/releases/?version=25> e baixe o instalador
   **`.msi`** para Windows x64, versão **25**.
2. Execute o `.msi`. Na tela de opções, marque **"Set JAVA_HOME variable"** e
   **"Add to PATH"** (assim o `java` e o `javac` ficam disponíveis no terminal).
3. Abra um **novo** PowerShell/CMD e verifique:

```powershell
java -version
javac -version
```

> **Importante no Windows:** sempre **feche e reabra** o terminal depois de
> instalar o JDK — só assim o `PATH`/`JAVA_HOME` atualizado é carregado. Se o
> `java -version` ainda não funcionar, confira se a variável de ambiente
> **`JAVA_HOME`** aponta para a pasta do JDK 25 (ex.:
> `C:\Program Files\Microsoft\jdk-25...`) e se o `...\bin` está no **Path**.

> No Windows, use **`gradlew.bat run`** (e não `./gradlew run`).

### Verificar se o JDK já está instalado (qualquer SO)

```bash
java -version
javac -version
```

Se ambos respondem com versão **25.x**, você já está pronto — pode pular para
[Como rodar](#como-rodar--modo-gráfico-gui).

---

## Como rodar — Modo texto (CLI)

O modo texto não depende do Gradle nem do Kotlin: compila e roda **apenas com o
JDK**, usando `javac` e `java` diretamente.

Entre na raiz do projeto (a pasta `gerenciador/`):

```bash
cd gerenciador-de-memoria/gerenciador
```

**1. Compilar o núcleo Java** (a partir da raiz do projeto):

```bash
javac -d build src/main/java/*.java
```

Isso gera os `.class` em `build/`.

**2. Executar passando um arquivo de entrada:**

```bash
java -cp build Main first_fit.txt
```

O projeto já inclui três arquivos de exemplo prontos, com os **mesmos eventos** e
diferindo apenas no algoritmo, para você comparar o comportamento lado a lado:

```bash
java -cp build Main first_fit.txt
java -cp build Main best_fit.txt
java -cp build Main worst_fit.txt
```

> **Sintaxe de uso:** `java -cp build Main <arquivo-de-entrada>`
> Se rodar sem argumentos, o programa imprime a forma de uso e encerra.

---

## Como rodar — Modo gráfico (GUI)

Esta é a forma mais simples: o Gradle Wrapper cuida de tudo (baixa Gradle,
Kotlin, Compose e dependências, compila e executa).

Entre na raiz do projeto (a pasta `gerenciador/`):

```bash
cd gerenciador-de-memoria/gerenciador
```

**Linux / macOS:**

```bash
./gradlew run
```

**Windows (PowerShell ou CMD):**

```bash
gradlew.bat run
```

> **Primeira execução é mais demorada:** o wrapper baixa o Gradle 9.5.1 e todas
> as dependências do Compose (algumas centenas de MB). Da segunda vez em diante é
> rápido, pois tudo fica em cache.

A janela do simulador abre com:

- **Aba Simulador:** escolhe o algoritmo, aloca informando _ID do processo_ e
  _tamanho_, e libera informando o _ID do processo_. O mapa da memória mostra os
  blocos ocupados (com ID) e as brechas livres, além de um painel de estatísticas
  atualizado a cada operação.

## Formato do arquivo de entrada

- Linhas em branco e linhas iniciadas por `#` são ignoradas (comentários).
- **Primeira linha útil:** tamanho total da memória e, opcionalmente, o algoritmo:

  ```
  MEMORIA <tamanho> [FIRST_FIT|BEST_FIT|WORST_FIT]
  ```

  Se o algoritmo for omitido, usa `FIRST_FIT`. Um algoritmo passado na **linha de
  comando** tem prioridade sobre o do arquivo.

- **Eventos**, um por linha:

  ```
  ALOCAR <idProcesso> <tamanho>
  LIBERAR <idProcesso>
  ```

Exemplo (`best_fit.txt`):

```
# Formato: MEMORIA <tamanho> [algoritmo]; ALOCAR <id> <tamanho>; LIBERAR <id>
MEMORIA 1000 BEST_FIT
ALOCAR 1 200
ALOCAR 2 50
ALOCAR 3 100
ALOCAR 4 50
ALOCAR 5 200
LIBERAR 1
LIBERAR 3
ALOCAR 10 80
ALOCAR 11 150
LIBERAR 5
ALOCAR 12 500
```

---

## Saída do modo texto

Após **cada passo**, o programa exibe o estado da memória: cada partição com sua
faixa de endereços, o tamanho, e se é uma **brecha livre** ou está ocupada por um
processo (com o ID). Falhas de alocação são reportadas distinguindo **falta de
espaço** de **fragmentação externa**.

Ao final, é impresso um **relatório** com: algoritmo usado, memória ocupada e
livre, **utilização (%)**, número de alocações
bem-sucedidas e número de processos não alocados (separados por causa).

---

## Estrutura do projeto

```
gerenciador/
├── src/main/java/
│   ├── Bloco.java               # nó da lista de blocos (com ID de processo)
│   ├── GerenciadorMemoria.java  # núcleo: algoritmos, alocação/liberação, estatísticas
│   └── Main.java                # simulador CLI dirigido por arquivo de entrada
├── src/main/kotlin/
│   ├── Main.kt                  # ponto de entrada da GUI (Compose Desktop)
│   ├── PageSimulador.kt         # tela do simulador (mapa de memória + estatísticas)
│   └── LeitorArquivo.kt         # leitura de arquivo de entrada na GUI
├── first_fit.txt               # exemplo de entrada — First Fit
├── best_fit.txt                # exemplo de entrada — Best Fit
├── worst_fit.txt               # exemplo de entrada — Worst Fit
├── build.gradle.kts            # configuração do build (toolchain, Compose, alvo da JVM)
├── settings.gradle.kts         # versões de Kotlin/Compose (via gradle.properties)
├── gradle.properties           # kotlin.version=2.2.20, compose.version=1.9.0
└── gradlew / gradlew.bat       # Gradle Wrapper (baixa o Gradle 9.5.1 automaticamente)
```

---

## Solução de problemas (troubleshooting)

### `Cannot find a Java installation ... matching: {languageVersion=25}`

```
Could not determine the dependencies of task ':compileKotlin'.
> Cannot find a Java installation on your machine ... matching:
  {languageVersion=25, vendor=any vendor, ...}.
  Toolchain download repositories have not been configured.
```

**Causa:** o build pede o **JDK 25** como toolchain, mas ele não está instalado e
o Gradle não está configurado para baixar JDKs sozinho.

**Solução A — instalar o JDK 25 (recomendada):** siga a seção
[Instalação dos pré-requisitos](#instalação-dos-pré-requisitos-passo-a-passo).
Confira os JDKs que o Gradle enxerga com:

```bash
./gradlew -q javaToolchains
```

**Solução B — usar um JDK que você já tem:** se você tem, por exemplo, o JDK 24
ou 26 instalado e não quer baixar o 25, edite o `build.gradle.kts` e troque as
referências de `25` pela versão que você possui. São **três pontos**:

```kotlin
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24)) // <- sua versão
    }
    // ...
}

kotlin {
    jvmToolchain(24) // <- sua versão
    // ...
}

val launcher25 = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(24)) // <- sua versão
}
```

> **Atenção ao alvo da JVM:** mantenha `val jvmTargetVersion = 24` como está. O
> compilador Kotlin (2.2.20) **não emite bytecode acima de 24**, então o alvo
> deve continuar 24 mesmo que a _toolchain_ seja 25 ou 26. Mexer só na toolchain
> é seguro; mexer no `jvmTargetVersion` para um valor maior quebra o build com
> erro de inconsistência entre Java e Kotlin.

### `Permission denied` ao rodar `./gradlew` (Linux/macOS)

Dê permissão de execução ao wrapper:

```bash
chmod +x gradlew
./gradlew run
```
