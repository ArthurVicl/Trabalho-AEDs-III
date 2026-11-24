
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    private final Arquivo<Livro> arqLivros;

    public LivroDAO() throws Exception {
        arqLivros = new Arquivo<>("livros", Livro.class.getConstructor());
    }

    public int create(Livro l) throws Exception { 
        return arqLivros.create(l); 
    }

    public Livro read(int id) throws Exception { 
        return arqLivros.read(id); 
    }

    public boolean update(Livro l) throws Exception { 
        return arqLivros.update(l); 
    }

    public boolean delete(int id) throws Exception { 
        return arqLivros.delete(id); 
    }

    public List<Livro> readAll() throws Exception {
        return arqLivros.readAll();
    }

    /**
     * Pesquisa livros cujo título contenha o padrão textual informado,
     * utilizando o algoritmo Knuth-Morris-Pratt (KMP) implementado do zero.
     * 
     * Requisito Fase IV.
     */
    public List<Livro> buscarPorTituloKMP(String padrao) throws Exception {
        List<Livro> todos = readAll();
        List<Livro> encontrados = new ArrayList<>();
        for (Livro l : todos) {
            if (KMP.contem(l.getTitulo(), padrao)) {
                encontrados.add(l);
            }
        }
        return encontrados;
    }

    public void close() throws Exception {
        if (arqLivros != null) arqLivros.close();
    }
}