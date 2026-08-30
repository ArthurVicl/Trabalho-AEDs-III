import java.util.Scanner;

public class MenuLivros {
    private LivroDAO livroDAO;
    private Scanner console;

    public MenuLivros() throws Exception {
        livroDAO = new LivroDAO();
        console = new Scanner(System.in);
    }

    public void menu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n--- GERENCIAMENTO DE BIBLIOTECA ---");
            System.out.println("1 - Adicionar novo Livro");
            System.out.println("2 - Buscar Livro por ID");
            System.out.println("3 - Atualizar Livro");
            System.out.println("4 - Excluir Livro");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            
            try {
                opcao = Integer.parseInt(console.nextLine());
                switch (opcao) {
                    case 1: inserirLivro(); break;
                    case 2: buscarLivro(); break;
                    case 3: atualizarLivro(); break;
                    case 4: excluirLivro(); break;
                    case 0: break;
                    default: System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro detalhado: ");
                e.printStackTrace(); 
            }
        }
    }

    private void inserirLivro() throws Exception {
        System.out.print("Título do Livro: ");
        String titulo = console.nextLine();
        System.out.print("Autor do Livro: ");
        String autor = console.nextLine();
        System.out.print("Preço: ");
        float preco = Float.parseFloat(console.nextLine());

        Livro novoLivro = new Livro(titulo, autor, preco);
        int idIdGerado = livroDAO.create(novoLivro);
        System.out.println("Livro inserido com sucesso! ID gerado: " + idIdGerado);
    }

    private void buscarLivro() throws Exception {
        System.out.print("Digite o ID do Livro: ");
        int id = Integer.parseInt(console.nextLine());
        Livro livro = livroDAO.read(id);
        if (livro != null) {
            System.out.println("\n--- Dados do Livro ---");
            System.out.println(livro);
        } else {
            System.out.println("Livro não encontrado.");
        }
    }

    private void atualizarLivro() throws Exception {
        System.out.print("Digite o ID do Livro que deseja atualizar: ");
        int id = Integer.parseInt(console.nextLine());
        Livro livro = livroDAO.read(id);
        
        if (livro != null) {
            System.out.println("Dados atuais: " + livro);
            System.out.print("Novo Título (deixe em branco para manter): ");
            String titulo = console.nextLine();
            if (!titulo.isEmpty()) livro.setTitulo(titulo);

            System.out.print("Novo Autor (deixe em branco para manter): ");
            String autor = console.nextLine();
            if (!autor.isEmpty()) livro.setAutor(autor);

            System.out.print("Novo Preço (digite -1 para manter): ");
            float preco = Float.parseFloat(console.nextLine());
            if (preco != -1) livro.setPreco(preco);

            if (livroDAO.update(livro)) {
                System.out.println("Livro atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar o Livro.");
            }
        } else {
            System.out.println("Livro não encontrado.");
        }
    }

    private void excluirLivro() throws Exception {
        System.out.print("Digite o ID do Livro a ser excluído: ");
        int id = Integer.parseInt(console.nextLine());
        if (livroDAO.delete(id)) {
            System.out.println("Livro excluído com sucesso!");
        } else {
            System.out.println("Livro não encontrado ou não pôde ser excluído.");
        }
    }
}