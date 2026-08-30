public class UsuarioDAO {

    private Arquivo<Usuario> arquivo;

    public UsuarioDAO() throws Exception {
        arquivo = new Arquivo<>(
            "usuarios",
            Usuario.class.getConstructor()
        );
    }

    public int create(Usuario usuario) throws Exception {
        return arquivo.create(usuario);
    }

    public Usuario read(int id) throws Exception {
        return arquivo.read(id);
    }

    public boolean update(Usuario usuario) throws Exception {
        return arquivo.update(usuario);
    }

    public boolean delete(int id) throws Exception {
        return arquivo.delete(id);
    }

    public void close() throws Exception {
        arquivo.close();
    }
}