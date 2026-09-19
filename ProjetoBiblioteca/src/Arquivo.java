import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class Arquivo<T extends Registro> {
    private static final int TAM_CABECALHO = 12;
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;

    private HashExtensivel indicePrimario;

    public Arquivo(String nomeArquivo, Constructor<T> construtor) throws Exception {
        File diretorio = new File("./dados");
        if (!diretorio.exists()) diretorio.mkdir();

        diretorio = new File("./dados/" + nomeArquivo);
        if (!diretorio.exists()) diretorio.mkdir();

        this.nomeArquivo = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".db";
        this.construtor = construtor;
        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

        // Inicializa o Hash passando o nome da entidade e a capacidade do bucket
        this.indicePrimario = new HashExtensivel(nomeArquivo, 50);

        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0);    // Último ID usado
            arquivo.writeLong(-1);  // Lista de registros excluídos
        }
    }

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int novoID = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);
        byte[] dados = obj.toByteArray();

        long endereco = getDeleted(dados.length);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');  // Lápide
            arquivo.writeShort(dados.length);
            arquivo.write(dados);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte(' ');  // Remove a lápide
            arquivo.skipBytes(2);
            arquivo.write(dados);
        }

        //Cadastra o ID e o endereço físico no Hash
        indicePrimario.create(obj.getId(), endereco);

        return obj.getId();
    }

    public T read(int id) throws Exception {
        // Consulta o Hash para descobrir onde o registro está
        long enderecoFisico = indicePrimario.read(id);
        
        if (enderecoFisico != -1) { // Se encontrou no índice
            arquivo.seek(enderecoFisico);
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();
            byte[] dados = new byte[tamanho];
            arquivo.read(dados);
            
            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(dados);
                return obj;
            }
        }
        return null;
    }

    public boolean delete(int id) throws Exception {
        // 1. Busca o endereço físico no índice
        long posicao = indicePrimario.read(id);
        
        // Se o índice retornou -1, o registro não existe ou foi apagado
        if (posicao == -1) {
            return false;
        }

        // 2. Vai direto para o endereço no arquivo físico
        arquivo.seek(posicao);
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();

        // 3. Se o registro estiver ativo, faz a exclusão
        if (lapide == ' ') {
            arquivo.seek(posicao);
            arquivo.writeByte('*'); // Marca como excluído
            addDeleted(tamanho, posicao); // Adiciona na lista de excluídos
            
            // 4. Remove o ID do índice primário para manter a consistência
            indicePrimario.delete(id);
            
            return true;
        }
        
        return false;
    }

    public boolean update(T novoObj) throws Exception {
        // 1. Busca o endereço atual do registro no índice (Hash)
        long posicao = indicePrimario.read(novoObj.getId());
        
        if (posicao == -1) {
            return false;
        }

        // 2. Vai direto para o endereço no arquivo físico
        arquivo.seek(posicao);
        byte lapide = arquivo.readByte();
        short tamanho = arquivo.readShort();

        if (lapide == ' ') {
            byte[] novosDados = novoObj.toByteArray();
            short novoTam = (short) novosDados.length;

            // 3. O novo registro é menor ou igual ao antigo?
            if (novoTam <= tamanho) {
                // Sobrescreve no mesmo lugar. O endereço físico não muda,
                // logo, o índice Hash NÃO precisa ser atualizado.
                arquivo.seek(posicao + 3);
                arquivo.write(novosDados);
            } 
            // 4. O novo registro é maior. Precisa ir para outro lugar.
            else {
                // Marca o espaço antigo como excluído
                arquivo.seek(posicao);
                arquivo.writeByte('*');
                addDeleted(tamanho, posicao);

                // Busca um novo espaço na lista de excluídos ou no fim do arquivo
                long novoEndereco = getDeleted(novosDados.length);
                if (novoEndereco == -1) {
                    arquivo.seek(arquivo.length());
                    novoEndereco = arquivo.getFilePointer(); // Pega o endereço no fim do arquivo
                    arquivo.writeByte(' ');
                    arquivo.writeShort(novoTam);
                    arquivo.write(novosDados);
                } else {
                    arquivo.seek(novoEndereco); // Vai para o espaço reaproveitado
                    arquivo.writeByte(' ');
                    arquivo.skipBytes(2);
                    arquivo.write(novosDados);
                }
                
                // 5. ATUALIZA O ÍNDICE: O registro mudou de lugar, então o Hash 
                // precisa apontar para o `novoEndereco`
                indicePrimario.update(novoObj.getId(), novoEndereco);
            }
            return true;
        }
        
        return false;
    }
    
    private void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;

        if (endereco == -1) {
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
        } else {
            do {
                arquivo.seek(endereco + 1);
                int tamanho = arquivo.readShort();
                proximo = arquivo.readLong();

                if (tamanho > tamanhoEspaco) {
                    if (posicao == 4)
                        arquivo.seek(posicao);
                    else
                        arquivo.seek(posicao + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(endereco);
                    break;
                }

                if (proximo == -1) {
                    arquivo.seek(endereco + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(-1);
                    break;
                }

                posicao = endereco;
                endereco = proximo;
            } while (endereco != -1);
        }
    }

    private long getDeleted(int tamanhoNecessario) throws Exception {
        long posicao = 4;
        arquivo.seek(posicao);
        long endereco = arquivo.readLong();
        long proximo;
        int tamanho;

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();

            if (tamanho > tamanhoNecessario) {
                if (posicao == 4)
                    arquivo.seek(posicao);
                else
                    arquivo.seek(posicao + 3);
                arquivo.writeLong(proximo);
                return endereco;
            }
            posicao = endereco;
            endereco = proximo;
        }
        return -1;
    }

    public void close() throws Exception {
        arquivo.close();
    }
}
