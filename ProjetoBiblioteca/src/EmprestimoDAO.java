public class EmprestimoDAO {

    private Arquivo<Emprestimo> arquivo;

    public EmprestimoDAO() throws Exception {
        arquivo = new Arquivo<>(
            "emprestimos",
            Emprestimo.class.getConstructor()
        );
    }

    public int create(Emprestimo emprestimo) throws Exception {
        return arquivo.create(emprestimo);
    }

    public Emprestimo read(int id) throws Exception {
        return arquivo.read(id);
    }

    public boolean update(Emprestimo emprestimo) throws Exception {
        return arquivo.update(emprestimo);
    }

    public boolean delete(int id) throws Exception {
        return arquivo.delete(id);
    }

    public void close() throws Exception {
        arquivo.close();
    }
}