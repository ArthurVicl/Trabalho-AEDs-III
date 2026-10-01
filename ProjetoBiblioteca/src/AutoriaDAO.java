import java.util.ArrayList;
import java.util.List;

public class AutoriaDAO {
    private Arquivo<Autoria> arqAutoria;
    private ArvoreBPlus indiceAutorLivro; // Chave: idAutor, Valor: idAutoria
    private ArvoreBPlus indiceLivroAutor; // Chave: idLivro, Valor: idAutoria

    public AutoriaDAO() throws Exception {
        arqAutoria = new Arquivo<>("autorias", Autoria.class.getConstructor());
        indiceAutorLivro = new ArvoreBPlus("indice_autoria_autor_livro", 8);
        indiceLivroAutor = new ArvoreBPlus("indice_autoria_livro_autor", 8);
    }

    public boolean create(int idAutor, int idLivro) throws Exception {
        // Garantindo que a associação N:N não seja cadastrada mais de uma vez (Chave Composta)
        List<Integer> autoriasDoAutor = indiceAutorLivro.read(idAutor);
        for (int idAutoria : autoriasDoAutor) {
            Autoria a = arqAutoria.read(idAutoria);
            if (a != null && a.getIdLivro() == idLivro) {
                return false; // Associação já existe
            }
        }

        // Insere no arquivo de dados
        Autoria a = new Autoria(idAutor, idLivro);
        int idAutoria = arqAutoria.create(a);

        // Atualiza as árvores B+ (índices em memória secundária)
        indiceAutorLivro.create(idAutor, idAutoria);
        indiceLivroAutor.create(idLivro, idAutoria);

        return true;
    }

    // Consulta: Listar livros escritos por um autor
    public List<Livro> readLivrosDoAutor(int idAutor, LivroDAO livroDAO) throws Exception {
        List<Integer> idsAutorias = indiceAutorLivro.read(idAutor);
        List<Livro> livros = new ArrayList<>();
        for (int idAutoria : idsAutorias) {
            Autoria a = arqAutoria.read(idAutoria);
            if (a != null) {
                Livro l = livroDAO.read(a.getIdLivro());
                if (l != null) livros.add(l);
            }
        }
        return livros;
    }

    // Consulta: Listar autores de um determinado livro
    public List<Autor> readAutoresDoLivro(int idLivro, AutorDAO autorDAO) throws Exception {
        List<Integer> idsAutorias = indiceLivroAutor.read(idLivro);
        List<Autor> autores = new ArrayList<>();
        for (int idAutoria : idsAutorias) {
            Autoria a = arqAutoria.read(idAutoria);
            if (a != null) {
                Autor au = autorDAO.read(a.getIdAutor());
                if (au != null) autores.add(au);
            }
        }
        return autores;
    }

    // Exclusão de uma associação específica
    public boolean delete(int idAutor, int idLivro) throws Exception {
        List<Integer> autoriasDoAutor = indiceAutorLivro.read(idAutor);
        for (int idAutoria : autoriasDoAutor) {
            Autoria a = arqAutoria.read(idAutoria);
            if (a != null && a.getIdLivro() == idLivro) {
                arqAutoria.delete(idAutoria);
                indiceAutorLivro.delete(idAutor, idAutoria);
                indiceLivroAutor.delete(idLivro, idAutoria);
                return true;
            }
        }
        return false;
    }

    // Integridade Referencial: Chamado quando um AUTOR for excluído
    public void deleteAllFromAutor(int idAutor) throws Exception {
        List<Integer> autoriasDoAutor = indiceAutorLivro.read(idAutor);
        for (int idAutoria : autoriasDoAutor) {
            Autoria a = arqAutoria.read(idAutoria);
            if (a != null) {
                int idLivro = a.getIdLivro();
                arqAutoria.delete(idAutoria);
                indiceLivroAutor.delete(idLivro, idAutoria);
            }
            indiceAutorLivro.delete(idAutor, idAutoria);
        }
    }

    // Integridade Referencial: Chamado quando um LIVRO for excluído
    public void deleteAllFromLivro(int idLivro) throws Exception {
        List<Integer> autoriasDoLivro = indiceLivroAutor.read(idLivro);
        for (int idAutoria : autoriasDoLivro) {
            Autoria a = arqAutoria.read(idAutoria);
            if (a != null) {
                int idAutor = a.getIdAutor();
                arqAutoria.delete(idAutoria);
                indiceAutorLivro.delete(idAutor, idAutoria);
            }
            indiceLivroAutor.delete(idLivro, idAutoria);
        }
    }
}
