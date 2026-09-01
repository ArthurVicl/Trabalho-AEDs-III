import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Autor implements Registro {
    private int id;
    private String nome;
    private String dataNascimento;
    private String nacionalidade;

    public Autor() {
        this(-1, "", "", "");
    }

    public Autor(String nome, String dataNascimento, String nacionalidade) {
        this(-1, nome, dataNascimento, nacionalidade);
    }

    public Autor(int id, String nome, String dataNascimento, String nacionalidade) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nacionalidade = nacionalidade;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(dataNascimento);
        dos.writeUTF(nacionalidade);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        nome = dis.readUTF();
        dataNascimento = dis.readUTF();
        nacionalidade = dis.readUTF();
    }

    @Override
    public String toString() {
        return "ID: " + id
            + " | Nome: " + nome
            + " | Data de Nascimento: " + dataNascimento
            + " | Nacionalidade: " + nacionalidade;
    }
}