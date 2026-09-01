import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Usuario implements Registro {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    public Usuario() {
        this(-1, "", "", "", "");
    }

    public Usuario(String nome, String cpf, String email, String telefone) {
        this(-1, nome, cpf, email, telefone);
    }

    public Usuario(int id, String nome, String cpf, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(cpf);
        dos.writeUTF(email);
        dos.writeUTF(telefone);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        nome = dis.readUTF();
        cpf = dis.readUTF();
        email = dis.readUTF();
        telefone = dis.readUTF();
    }

    @Override
    public String toString() {
        return "ID: " + id
            + " | Nome: " + nome
            + " | CPF: " + cpf
            + " | E-mail: " + email
            + " | Telefone: " + telefone;
    }
}