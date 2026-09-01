public class AutorDAO {

    private Arquivo<Autor> arquivo;

    public AutorDAO() throws Exception {
        arquivo = new Arquivo<>(
            "autores",
            Autor.class.getConstructor()
        );
    }

    public int create(Autor autor) throws Exception {
        return arquivo.create(autor);
    }

    public Autor read(int id) throws Exception {
        return arquivo.read(id);
    }

    public boolean update(Autor autor) throws Exception {
        return arquivo.update(autor);
    }

    public boolean delete(int id) throws Exception {
        return arquivo.delete(id);
    }

    public void close() throws Exception {
        arquivo.close();
    }
}