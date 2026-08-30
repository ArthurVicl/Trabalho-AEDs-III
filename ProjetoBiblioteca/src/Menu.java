import java.util.Scanner;
import java.io.IOException;


public class Menu {

    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;
    private AutorDAO autorDAO;
    private EmprestimoDAO emprestimoDAO;

    private Scanner console;

    public Menu() throws Exception {
        livroDAO = new LivroDAO();
        usuarioDAO = new UsuarioDAO();
        autorDAO = new AutorDAO();
        emprestimoDAO = new EmprestimoDAO();

        console = new Scanner(System.in);
    }

    public void menu() {

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n==============================");
            System.out.println("     SISTEMA DE BIBLIOTECA");
            System.out.println("==============================");
            System.out.println("1 - Gerenciar Livros");
            System.out.println("2 - Gerenciar Usuários");
            System.out.println("3 - Gerenciar Autores");
            System.out.println("4 - Gerenciar Empréstimos");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            try {

                opcao = Integer.parseInt(console.nextLine());

                switch (opcao) {

                    case 1:
                        menuLivros();
                        break;

                    case 2:
                        menuUsuarios();
                        break;

                    case 3:
                        menuAutores();
                        break;

                    case 4:
                        menuEmprestimos();
                        break;

                    case 0:
                        System.out.println("Encerrando o sistema...");
                        break;

                    default:
                        System.out.println("Opção inválida!");
                }

            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    // =====================================================
    // LIVROS
    // =====================================================

    private void menuLivros() throws Exception {

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n--- GERENCIAMENTO DE LIVROS ---");
            System.out.println("1 - Adicionar novo Livro");
            System.out.println("2 - Buscar Livro por ID");
            System.out.println("3 - Atualizar Livro");
            System.out.println("4 - Excluir Livro");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(console.nextLine());

            switch (opcao) {

                case 1:
                    inserirLivro();
                    break;

                case 2:
                    buscarLivro();
                    break;

                case 3:
                    atualizarLivro();
                    break;

                case 4:
                    excluirLivro();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

   private void inserirLivro() throws Exception {

    System.out.print("Título do Livro: ");
    String titulo = console.nextLine();

    System.out.print("ID do Autor: ");
    int idAutor = Integer.parseInt(console.nextLine());

    Autor autor = autorDAO.read(idAutor);

    if (autor == null) {
        System.out.println("Autor não encontrado. Livro não cadastrado.");
        return;
    }

    System.out.print("Data de Publicação: ");
    String dataPublicacao = console.nextLine();

    System.out.print("Categorias: ");
    String categorias = console.nextLine();

    System.out.print("Avaliação: ");
    float avaliacao = Float.parseFloat(console.nextLine());

    System.out.print("Preço: ");
    float preco = Float.parseFloat(console.nextLine());

    Livro livro = new Livro(
        titulo,
        idAutor,
        dataPublicacao,
        categorias,
        avaliacao,
        preco
    );

    int id = livroDAO.create(livro);

    System.out.println("Livro inserido com sucesso! ID: " + id);
}
    private void buscarLivro() throws Exception {

        System.out.print("Digite o ID do Livro: ");
        int id = Integer.parseInt(console.nextLine());

        Livro livro = livroDAO.read(id);

        if (livro != null)
            System.out.println(livro);
        else
            System.out.println("Livro não encontrado.");
    }

    private void atualizarLivro() throws Exception {

    System.out.print("Digite o ID do Livro: ");
    int id = Integer.parseInt(console.nextLine());

    Livro livro = livroDAO.read(id);

    if (livro != null) {

        System.out.print("Novo Título: ");
        livro.setTitulo(console.nextLine());

        System.out.print("Novo ID do Autor: ");
        int idAutor = Integer.parseInt(console.nextLine());

        Autor autor = autorDAO.read(idAutor);

        if (autor == null) {
            System.out.println("Autor não encontrado. Livro não atualizado.");
            return;
        }

        livro.setIdAutor(idAutor);

        System.out.print("Nova Data de Publicação: ");
        livro.setDataPublicacao(console.nextLine());

        System.out.print("Novas Categorias: ");
        livro.setCategorias(console.nextLine());

        System.out.print("Nova Avaliação: ");
        livro.setAvaliacao(
            Float.parseFloat(console.nextLine())
        );

        System.out.print("Novo Preço: ");
        livro.setPreco(
            Float.parseFloat(console.nextLine())
        );

        if (livroDAO.update(livro)) {
            System.out.println("Livro atualizado com sucesso!");
        }

    } else {
        System.out.println("Livro não encontrado.");
    }
}

    private void excluirLivro() throws Exception {

        System.out.print("Digite o ID do Livro: ");
        int id = Integer.parseInt(console.nextLine());

        if (livroDAO.delete(id))
            System.out.println("Livro excluído com sucesso!");
        else
            System.out.println("Livro não encontrado.");
    }

    // =====================================================
    // USUÁRIOS
    // =====================================================

    private void menuUsuarios() throws Exception {

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n--- GERENCIAMENTO DE USUÁRIOS ---");
            System.out.println("1 - Adicionar novo Usuário");
            System.out.println("2 - Buscar Usuário por ID");
            System.out.println("3 - Atualizar Usuário");
            System.out.println("4 - Excluir Usuário");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(console.nextLine());

            switch (opcao) {

                case 1:
                    inserirUsuario();
                    break;

                case 2:
                    buscarUsuario();
                    break;

                case 3:
                    atualizarUsuario();
                    break;

                case 4:
                    excluirUsuario();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void inserirUsuario() throws Exception {

        System.out.print("Nome: ");
        String nome = console.nextLine();

        System.out.print("CPF: ");
        String cpf = console.nextLine();

        System.out.print("E-mail: ");
        String email = console.nextLine();

        System.out.print("Telefone: ");
        String telefone = console.nextLine();

        Usuario usuario =
            new Usuario(nome, cpf, email, telefone);

        int id = usuarioDAO.create(usuario);

        System.out.println("Usuário inserido! ID: " + id);
    }

    private void buscarUsuario() throws Exception {

        System.out.print("Digite o ID do Usuário: ");
        int id = Integer.parseInt(console.nextLine());

        Usuario usuario = usuarioDAO.read(id);

        if (usuario != null)
            System.out.println(usuario);
        else
            System.out.println("Usuário não encontrado.");
    }

    private void atualizarUsuario() throws Exception {

        System.out.print("Digite o ID do Usuário: ");
        int id = Integer.parseInt(console.nextLine());

        Usuario usuario = usuarioDAO.read(id);

        if (usuario != null) {

            System.out.print("Novo Nome: ");
            usuario.setNome(console.nextLine());

            System.out.print("Novo CPF: ");
            usuario.setCpf(console.nextLine());

            System.out.print("Novo E-mail: ");
            usuario.setEmail(console.nextLine());

            System.out.print("Novo Telefone: ");
            usuario.setTelefone(console.nextLine());

            if (usuarioDAO.update(usuario))
                System.out.println("Usuário atualizado!");

        } else {
            System.out.println("Usuário não encontrado.");
        }
    }

    private void excluirUsuario() throws Exception {

        System.out.print("Digite o ID do Usuário: ");
        int id = Integer.parseInt(console.nextLine());

        if (usuarioDAO.delete(id))
            System.out.println("Usuário excluído!");
        else
            System.out.println("Usuário não encontrado.");
    }

    // =====================================================
    // AUTORES
    // =====================================================

    private void menuAutores() throws Exception {

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n--- GERENCIAMENTO DE AUTORES ---");
            System.out.println("1 - Adicionar novo Autor");
            System.out.println("2 - Buscar Autor por ID");
            System.out.println("3 - Atualizar Autor");
            System.out.println("4 - Excluir Autor");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(console.nextLine());

            switch (opcao) {

                case 1:
                    inserirAutor();
                    break;

                case 2:
                    buscarAutor();
                    break;

                case 3:
                    atualizarAutor();
                    break;

                case 4:
                    excluirAutor();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void inserirAutor() throws Exception {

        System.out.print("Nome: ");
        String nome = console.nextLine();

        System.out.print("Data de nascimento: ");
        String dataNascimento = console.nextLine();

        System.out.print("Nacionalidade: ");
        String nacionalidade = console.nextLine();

        Autor autor =
            new Autor(nome, dataNascimento, nacionalidade);

        int id = autorDAO.create(autor);

        System.out.println("Autor inserido! ID: " + id);
    }

    private void buscarAutor() throws Exception {

        System.out.print("Digite o ID do Autor: ");
        int id = Integer.parseInt(console.nextLine());

        Autor autor = autorDAO.read(id);

        if (autor != null)
            System.out.println(autor);
        else
            System.out.println("Autor não encontrado.");
    }

    private void atualizarAutor() throws Exception {

        System.out.print("Digite o ID do Autor: ");
        int id = Integer.parseInt(console.nextLine());

        Autor autor = autorDAO.read(id);

        if (autor != null) {

            System.out.print("Novo Nome: ");
            autor.setNome(console.nextLine());

            System.out.print("Nova Data de nascimento: ");
            autor.setDataNascimento(console.nextLine());

            System.out.print("Nova Nacionalidade: ");
            autor.setNacionalidade(console.nextLine());

            if (autorDAO.update(autor))
                System.out.println("Autor atualizado!");

        } else {
            System.out.println("Autor não encontrado.");
        }
    }

    private void excluirAutor() throws Exception {

        System.out.print("Digite o ID do Autor: ");
        int id = Integer.parseInt(console.nextLine());

        if (autorDAO.delete(id))
            System.out.println("Autor excluído!");
        else
            System.out.println("Autor não encontrado.");
    }

    // =====================================================
    // EMPRÉSTIMOS
    // =====================================================

    private void menuEmprestimos() throws Exception {

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n--- GERENCIAMENTO DE EMPRÉSTIMOS ---");
            System.out.println("1 - Adicionar novo Empréstimo");
            System.out.println("2 - Buscar Empréstimo por ID");
            System.out.println("3 - Atualizar Empréstimo");
            System.out.println("4 - Excluir Empréstimo");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(console.nextLine());

            switch (opcao) {

                case 1:
                    inserirEmprestimo();
                    break;

                case 2:
                    buscarEmprestimo();
                    break;

                case 3:
                    atualizarEmprestimo();
                    break;

                case 4:
                    excluirEmprestimo();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void inserirEmprestimo() throws Exception {

    System.out.print("ID do Usuário: ");
    int idUsuario = Integer.parseInt(console.nextLine());

    // Verifica se o usuário existe
    Usuario usuario = usuarioDAO.read(idUsuario);

    if (usuario == null) {
        System.out.println("Usuário não encontrado. Empréstimo não realizado.");
        return;
    }

    System.out.print("ID do Livro: ");
    int idLivro = Integer.parseInt(console.nextLine());

    // Verifica se o livro existe
    Livro livro = livroDAO.read(idLivro);

    if (livro == null) {
        System.out.println("Livro não encontrado. Empréstimo não realizado.");
        return;
    }

    System.out.print("Data do Empréstimo: ");
    String dataEmprestimo = console.nextLine();

    System.out.print("Data da Devolução: ");
    String dataDevolucao = console.nextLine();

    System.out.print("Valor da Multa: ");
    float valorMulta = Float.parseFloat(console.nextLine());

    Emprestimo emprestimo = new Emprestimo(
        idUsuario,
        idLivro,
        dataEmprestimo,
        dataDevolucao,
        valorMulta
    );

    int id = emprestimoDAO.create(emprestimo);

    System.out.println(
        "Empréstimo realizado com sucesso! ID: " + id
    );
}

    private void buscarEmprestimo() throws Exception {

        System.out.print("Digite o ID do Empréstimo: ");
        int id = Integer.parseInt(console.nextLine());

        Emprestimo emprestimo =
            emprestimoDAO.read(id);

        if (emprestimo != null)
            System.out.println(emprestimo);
        else
            System.out.println("Empréstimo não encontrado.");
    }

    private void atualizarEmprestimo() throws Exception {

        System.out.print("Digite o ID do Empréstimo: ");
        int id = Integer.parseInt(console.nextLine());

        Emprestimo emprestimo =
            emprestimoDAO.read(id);

        if (emprestimo != null) {

            System.out.print("Novo ID do Usuário: ");
            emprestimo.setIdUsuario(
                Integer.parseInt(console.nextLine())
            );

            System.out.print("Novo ID do Livro: ");
            emprestimo.setIdLivro(
                Integer.parseInt(console.nextLine())
            );

            System.out.print("Nova Data do Empréstimo: ");
            emprestimo.setDataEmprestimo(
                console.nextLine()
            );

            System.out.print("Nova Data da Devolução: ");
            emprestimo.setDataDevolucao(
                console.nextLine()
            );

            System.out.print("Novo Valor da Multa: ");
            emprestimo.setValorMulta(
                Float.parseFloat(console.nextLine())
            );

            if (emprestimoDAO.update(emprestimo))
                System.out.println("Empréstimo atualizado!");

        } else {
            System.out.println("Empréstimo não encontrado.");
        }
    }

    private void excluirEmprestimo() throws Exception {

        System.out.print("Digite o ID do Empréstimo: ");
        int id = Integer.parseInt(console.nextLine());

        if (emprestimoDAO.delete(id))
            System.out.println("Empréstimo excluído!");
        else
            System.out.println("Empréstimo não encontrado.");
    }
}