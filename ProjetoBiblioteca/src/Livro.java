import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Livro implements Registro {

    private int id;
    private String titulo;
    private String dataPublicacao;
    private String categorias;
    private float avaliacao;
    private float preco;

    public Livro() {
        this(-1, "", "", "", 0F, 0F);
    }

    public Livro(
        String titulo,
        String dataPublicacao,
        String categorias,
        float avaliacao,
        float preco
    ) {
        this(
            -1,
            titulo,
            dataPublicacao,
            categorias,
            avaliacao,
            preco
        );
    }

    public Livro(
        int id,
        String titulo,
        String dataPublicacao,
        String categorias,
        float avaliacao,
        float preco
    ) {
        this.id = id;
        this.titulo = titulo;
        this.dataPublicacao = dataPublicacao;
        this.categorias = categorias;
        this.avaliacao = avaliacao;
        this.preco = preco;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getCategorias() {
        return categorias;
    }

    public void setCategorias(String categorias) {
        this.categorias = categorias;
    }

    public float getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(float avaliacao) {
        this.avaliacao = avaliacao;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    @Override
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(titulo);
        dos.writeUTF(dataPublicacao);
        dos.writeUTF(categorias);
        dos.writeFloat(avaliacao);
        dos.writeFloat(preco);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        titulo = dis.readUTF();
        dataPublicacao = dis.readUTF();
        categorias = dis.readUTF();
        avaliacao = dis.readFloat();
        preco = dis.readFloat();
    }

    @Override
    public String toString() {

        return "ID: " + id
            + " | Título: " + titulo
            + " | Data Publicação: " + dataPublicacao
            + " | Categorias: " + categorias
            + " | Avaliação: " + avaliacao
            + " | Preço: R$ " + preco;
    }
}