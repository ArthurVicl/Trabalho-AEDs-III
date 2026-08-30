public class LivroDAO {
    private Arquivo<Livro> arqLivros;

    public LivroDAO() throws Exception {
        arqLivros = new Arquivo<>("livros", Livro.class.getConstructor());
    }

    // Agora os nomes batem com o que você usou no Menu
    public int create(Livro l) throws Exception { return arqLivros.create(l); }
    public Livro read(int id) throws Exception { return arqLivros.read(id); }
    public boolean update(Livro l) throws Exception { return arqLivros.update(l); }
    public boolean delete(int id) throws Exception { return arqLivros.delete(id); }
}