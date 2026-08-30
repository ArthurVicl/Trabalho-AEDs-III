import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Emprestimo implements Registro {
    private int id;
    private int idUsuario;
    private int idLivro;
    private String dataEmprestimo;
    private String dataDevolucao;
    private float valorMulta;

    public Emprestimo() {
        this(-1, -1, -1, "", "", 0F);
    }

    public Emprestimo(int idUsuario, int idLivro, String dataEmprestimo, String dataDevolucao, float valorMulta) {
        this(-1, idUsuario, idLivro, dataEmprestimo, dataDevolucao, valorMulta);
    }

    public Emprestimo(int id, int idUsuario, int idLivro, String dataEmprestimo, String dataDevolucao, float valorMulta) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idLivro = idLivro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.valorMulta = valorMulta;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public String getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(String dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public String getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(String dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public float getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(float valorMulta) {
        this.valorMulta = valorMulta;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeInt(idLivro);
        dos.writeUTF(dataEmprestimo);
        dos.writeUTF(dataDevolucao);
        dos.writeFloat(valorMulta);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        idUsuario = dis.readInt();
        idLivro = dis.readInt();
        dataEmprestimo = dis.readUTF();
        dataDevolucao = dis.readUTF();
        valorMulta = dis.readFloat();
    }

    @Override
    public String toString() {
        return "ID: " + id
            + " | Usuário: " + idUsuario
            + " | Livro: " + idLivro
            + " | Data Empréstimo: " + dataEmprestimo
            + " | Data Devolução: " + dataDevolucao
            + " | Multa: R$ " + valorMulta;
    }
}