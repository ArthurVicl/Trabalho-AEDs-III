# Sistema de Gerenciamento de Biblioteca

Trabalho Prático da disciplina de **Algoritmos e Estruturas de Dados III (AEDs III)**  
**Pontifícia Universidade Católica de Minas Gerais (PUC Minas)** — Instituto de Ciências Exatas e Informática (ICEI)

---

## 📌 Sobre o Projeto

O sistema é uma aplicação completa de gerenciamento de biblioteca desenvolvida em **Java** com interface gráfica **Swing**. O projeto implementa persistência de dados em arquivos binários próprios (com registro de tamanho variável, cabeçalho de controle e exclusão lógica por lápide), além de indexação por **Hashing Extensível** e **Árvores B+**.

Na **Fase III**, foi implementado o relacionamento **N:N (Autoria)** entre `Autor` e `Livro`, com arquivo intermediário binário e indexação bidirecional por Árvores B+.

---

## 🧱 Arquitetura e Estruturas de Dados

- **Camada de Apresentação (GUI):** Interface gráfica desenvolvida em Java Swing (`MenuGUI.java`), sem uso de console.
- **Camada de Negócio / DAOs:** `LivroDAO`, `AutorDAO`, `UsuarioDAO`, `EmprestimoDAO` e `AutoriaDAO`.
- **Camada de Persistência:** Classe genérica `Arquivo<T>` que gerencia a escrita/leitura binária com reaproveitamento de espaço via lista de registros excluídos.
- **Indexação Primária:** **Hashing Extensível** (`HashExtensivel.java`) com arquivos de diretório (`.dir`) e cestos (`.hash`).
- **Indexação Secundária / Relacionamento N:N:** **Árvores B+** (`ArvoreBPlus.java`) mantidas em memória secundária (`.btree`):
  - `indice_autoria_autor_livro`: Chave = `idAutor`, Valor = `idAutoria`
  - `indice_autoria_livro_autor`: Chave = `idLivro`, Valor = `idAutoria`

---

## 🚀 Instruções de Compilação e Execução

### Pré-requisitos
- **Java Development Kit (JDK 8 ou superior)** instalado e configurado no PATH do sistema.

---

### Passo 1: Abrir o terminal no diretório do projeto

```bash
cd ProjetoBiblioteca
```

---

### Passo 2: Compilar os arquivos-fonte

Crie o diretório de saída de classes (caso não exista) e compile:

**No Windows (PowerShell / Prompt de Comando):**
```powershell
javac -d bin src/*.java
```

> **Nota:** Caso utilize versões mais recentes do JDK (JDK 11, 17 ou 21), você pode compilar com compatibilidade para Java 8:
> ```powershell
> javac --release 8 -d bin src/*.java
> ```

---

### Passo 3: Executar a Aplicação

Para iniciar o sistema com a Interface Gráfica:

```powershell
java -cp bin Main
```

---

### 💡 (Opcional) Popular o banco com dados de exemplo

Caso deseje carregar autores, livros, usuários e vínculos iniciais para testes:

```powershell
java -cp bin PopulateDB
```

---

## 📂 Estrutura de Diretórios

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
