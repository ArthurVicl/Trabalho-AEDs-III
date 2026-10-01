public class PopulateDB {
    public static void main(String[] args) {
        try {
            System.out.println("Limpando bases e populando com exemplos...");
            
            AutorDAO autorDAO = new AutorDAO();
            LivroDAO livroDAO = new LivroDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
            
            // Inserir Autores
            int idJRR = autorDAO.create(new Autor("J.R.R. Tolkien", "03/01/1892", "Britânico"));
            int idJK = autorDAO.create(new Autor("J.K. Rowling", "31/07/1965", "Britânica"));
            int idMachado = autorDAO.create(new Autor("Machado de Assis", "21/06/1839", "Brasileiro"));
            
            // Inserir Livros
            AutoriaDAO autoriaDAO = new AutoriaDAO();
            int idSenhor = livroDAO.create(new Livro("O Senhor dos Anéis: A Sociedade do Anel", "29/07/1954", "Fantasia, Aventura", 4.9f, 60.50f));
            autoriaDAO.create(idJRR, idSenhor);
            
            int idHobbit = livroDAO.create(new Livro("O Hobbit", "21/09/1937", "Fantasia, Aventura", 4.8f, 45.00f));
            autoriaDAO.create(idJRR, idHobbit);
            
            int idHarry = livroDAO.create(new Livro("Harry Potter e a Pedra Filosofal", "26/06/1997", "Fantasia, Magia", 4.7f, 35.90f));
            autoriaDAO.create(idJK, idHarry);
            
            int idBras = livroDAO.create(new Livro("Memórias Póstumas de Brás Cubas", "01/01/1881", "Ficção, Romance", 4.9f, 25.00f));
            autoriaDAO.create(idMachado, idBras);
            
            // Inserir Usuários
            int idJoao = usuarioDAO.create(new Usuario("João Silva", "111.111.111-11", "joao@email.com", "(11) 91111-1111"));
            int idMaria = usuarioDAO.create(new Usuario("Maria Oliveira", "222.222.222-22", "maria@email.com", "(22) 92222-2222"));
            int idCarlos = usuarioDAO.create(new Usuario("Carlos Souza", "333.333.333-33", "carlos@email.com", "(33) 93333-3333"));
            
            // Inserir Empréstimos
            emprestimoDAO.create(new Emprestimo(idJoao, idSenhor, "01/10/2026", "15/10/2026", 0.0f));
            emprestimoDAO.create(new Emprestimo(idMaria, idHarry, "05/10/2026", "20/10/2026", 5.50f));
            
            System.out.println("Exemplos criados com sucesso!");
            
        } catch (Exception e) {
            System.err.println("Erro ao popular o banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
