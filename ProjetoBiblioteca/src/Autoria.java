import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Autoria implements Registro {
    private int id; // ID interno gerado pelo Arquivo.java
    private int idAutor;
    private int idLivro;

    public Autoria() {
        this(-1, -1, -1);
    }

    public Autoria(int idAutor, int idLivro) {
        this(-1, idAutor, idLivro);
    }

    public Autoria(int id, int idAutor, int idLivro) {
        this.id = id;
        this.idAutor = idAutor;
        this.idLivro = idLivro;
    }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public int getId() { return id; }

    public int getIdAutor() { return idAutor; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }

    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idAutor);
        dos.writeInt(idLivro);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idAutor = dis.readInt();
        idLivro = dis.readInt();
    }
}
