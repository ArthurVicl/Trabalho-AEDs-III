import java.util.ArrayList;
import java.util.List;

public class LivroDAO {
    private Arquivo<Livro> arqLivros;
    private ArvoreBPlus indiceAutorLivro;

    public LivroDAO() throws Exception {
        // Inicializa o acesso ao ficheiro de livros e o Hash Extensível internamente
        arqLivros = new Arquivo<>("livros", Livro.class.getConstructor());
        
        // Inicializa a Árvore B+ para indexar a relação (idAutor -> idLivro) com ordem 8
        indiceAutorLivro = new ArvoreBPlus("indice_autor_livro", 8);
    }

    public int create(Livro l) throws Exception { 
        // 1. Grava o livro no ficheiro .db e obtém o ID gerado
        int idLivro = arqLivros.create(l); 
        
        // 2. Regista a associação 1:N no índice B+
        indiceAutorLivro.create(l.getIdAutor(), idLivro);
        
        return idLivro; 
    }

    public Livro read(int id) throws Exception { 
        // A leitura direta por ID utiliza o Hash Extensível (O(1)) implementado dentro de Arquivo.java
        return arqLivros.read(id); 
    }

    // Método exclusivo para a consulta 1:N da rubrica
    public List<Livro> readByAutor(int idAutor) throws Exception {
        // 1. Obtém a lista de IDs de livros encadeados nas folhas da Árvore B+
        List<Integer> idsLivros = indiceAutorLivro.read(idAutor);
        List<Livro> livrosDoAutor = new ArrayList<>();
        
        // 2. Instancia cada livro a partir do seu ID
        for (int idLivro : idsLivros) {
            Livro livro = arqLivros.read(idLivro);
            if (livro != null) {
                livrosDoAutor.add(livro);
            }
        }
        return livrosDoAutor;
    }

    public boolean update(Livro l) throws Exception { 
        // 1. Recupera o livro antigo ANTES da atualização para verificar se a FK (idAutor) mudou
        Livro livroAntigo = arqLivros.read(l.getId());
        
        if (livroAntigo == null) {
            return false; // Livro não existe
        }

        // 2. Tenta efetuar a atualização no ficheiro .db
        boolean sucesso = arqLivros.update(l); 
        
        // 3. Se a atualização funcionou e o autor foi alterado, o índice deve ser corrigido
        if (sucesso && livroAntigo.getIdAutor() != l.getIdAutor()) {
            indiceAutorLivro.delete(livroAntigo.getIdAutor(), l.getId()); // Remove a chave antiga
            indiceAutorLivro.create(l.getIdAutor(), l.getId());           // Insere a nova chave
        }
        
        return sucesso;
    }

    public boolean delete(int id) throws Exception { 
        // 1. Recupera o livro antes de apagá-lo para saber qual idAutor remover da Árvore B+
        Livro livroAntigo = arqLivros.read(id);
        
        if (livroAntigo == null) {
            return false;
        }

        // 2. Efetua a exclusão lógica no ficheiro .db (lápide '*') e atualiza o Hash Extensível
        boolean sucesso = arqLivros.delete(id); 
        
        // 3. Remove o par exato da Árvore B+
        if (sucesso) {
            indiceAutorLivro.delete(livroAntigo.getIdAutor(), id);
        }
        
        return sucesso; 
    }
}