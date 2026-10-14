# Sistema de Gerenciamento de Biblioteca

Trabalho Prático da disciplina de **Algoritmos e Estruturas de Dados III (AEDs III)**  
**Pontifícia Universidade Católica de Minas Gerais (PUC Minas)** — Instituto de Ciências Exatas e Informática (ICEI)

---

## Sobre o Projeto

O sistema é uma aplicação de gerenciamento de biblioteca desenvolvida em **Java** com interface gráfica **Swing**. O projeto implementa persistência de dados em arquivos binários próprios (com registro de tamanho variável, cabeçalho de controle e exclusão lógica por lápide), além de indexação por **Hashing Extensível** e **Árvores B+**.

Na **Fase III**, foi implementado o relacionamento **N:N (Autoria)** entre `Autor` e `Livro`, com arquivo intermediário binário e indexação bidirecional por Árvores B+.

---

## Arquitetura e Estruturas de Dados

- **Camada de Apresentação (GUI):** Interface gráfica desenvolvida em Java Swing (`MenuGUI.java`), sem uso de console.
- **Camada de Negócio / DAOs:** `LivroDAO`, `AutorDAO`, `UsuarioDAO`, `EmprestimoDAO` e `AutoriaDAO`.
- **Camada de Persistência:** Classe genérica `Arquivo<T>` que gerencia a escrita e leitura binária com reaproveitamento de espaço via lista de registros excluídos.
- **Indexação Primária:** **Hashing Extensível** (`HashExtensivel.java`) com arquivos de diretório (`.dir`) e cestos (`.hash`).
- **Indexação Secundária / Relacionamento N:N:** **Árvores B+** (`ArvoreBPlus.java`) mantidas em memória secundária (`.btree`):
  - `indice_autoria_autor_livro`: Chave = `idAutor`, Valor = `idAutoria`
  - `indice_autoria_livro_autor`: Chave = `idLivro`, Valor = `idAutoria`

---

## Pré-requisitos

- **Java Development Kit (JDK 8 ou superior)** instalado na máquina.
- (Opcional) Uma IDE com suporte a Java (VS Code, Eclipse ou IntelliJ IDEA).

---

## Instruções de Execução

### Opção 1: Sem usar o terminal (Via IDE)

#### No Visual Studio Code (VS Code):
1. Abra a pasta `Trabalho-AEDs-III` no VS Code.
2. Certifique-se de que a extensão oficial **Extension Pack for Java** está instalada.
3. No explorador de arquivos, navegue até a pasta `ProjetoBiblioteca/src/` e abra o arquivo `Main.java`.
4. Clique no botão **Run** (Executar) que aparece logo acima do método `public static void main(String[] args)` ou pressione `F5`.
5. A interface gráfica do sistema será iniciada automaticamente.
6. *(Opcional)* Para preencher a base de dados com exemplos de teste, abra o arquivo `PopulateDB.java` dentro de `ProjetoBiblioteca/src/` e clique em **Run**.

#### No Eclipse / IntelliJ IDEA:
1. Abra a IDE e importe a pasta `ProjetoBiblioteca` como um projeto Java existente.
2. Localize o arquivo `Main.java` dentro do pacote padrão na pasta `src`.
3. Clique com o botão direito sobre o arquivo `Main.java` e selecione **Run As > Java Application** (no Eclipse) ou clique no ícone verde de **Run / Play** (no IntelliJ).

---

### Opção 2: Usando o terminal (Linha de Comando)

#### 1. Navegar até o diretório do projeto:
Abra o terminal (PowerShell, Prompt de Comando ou Bash) e entre na pasta `ProjetoBiblioteca`:

```bash
cd ProjetoBiblioteca
```

#### 2. Compilar os arquivos-fonte:
Crie o diretório de destino dos binários e compile todos os arquivos `.java`:

```bash
javac -d bin src/*.java
```

> **Nota:** Caso utilize versões mais recentes do JDK (JDK 11, 17 ou 21), execute com compatibilidade para Java 8:
> ```bash
> javac --release 8 -d bin src/*.java
> ```

#### 3. Executar o sistema:
Inicie a aplicação informando o classpath do diretório de classes compiladas:

```bash
java -cp bin Main
```

#### 4. (Opcional) Popular o banco com dados de teste via terminal:
```bash
java -cp bin PopulateDB
```

---

## Estrutura de Diretórios

```text
Trabalho-AEDs-III/
├── README.md                      # Instruções e documentação do projeto
├── DER_AEDs_III.png               # Diagrama Entidade-Relacionamento
├── UML_AED's_III.png              # Diagrama de Classes UML
├── dados/                         # Arquivos binários (.db, .hash, .dir, .btree)
└── ProjetoBiblioteca/
    ├── bin/                       # Bytecodes compilados (.class)
    ├── src/                       # Código-fonte Java
    │   ├── Main.java              # Ponto de entrada (inicializa MenuGUI)
    │   ├── MenuGUI.java           # Interface Gráfica principal
    │   ├── Autoria.java           # Entidade do relacionamento N:N
    │   ├── AutoriaDAO.java        # Regras e índices B+ do relacionamento N:N
    │   ├── Livro.java / LivroDAO.java
    │   ├── Autor.java / AutorDAO.java
    │   ├── Usuario.java / UsuarioDAO.java
    │   ├── Emprestimo.java / EmprestimoDAO.java
    │   ├── Arquivo.java / Registro.java
    │   ├── ArvoreBPlus.java       # Implementação da Árvore B+
    │   ├── HashExtensivel.java    # Implementação do Hashing Extensível
    │   ├── OrdenacaoExternaUsuario.java # Intercalação Balanceada
    │   └── PopulateDB.java        # Script de carga inicial de testes
    └── logo_biblioteca.png        # Imagem utilizada na interface
```
