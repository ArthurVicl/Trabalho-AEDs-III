import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class HashExtensivel {

    private String nomeArquivoDiretorio;
    private String nomeArquivoCestos;
    private int capacidadeCesto;
    private RandomAccessFile arqDiretorio;
    private RandomAccessFile arqCestos;
    private int profundidadeGlobal;

    public HashExtensivel(String nomeArquivo, int capacidadeCesto) throws Exception {
        this.capacidadeCesto = capacidadeCesto;
        this.nomeArquivoDiretorio = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".dir";
        this.nomeArquivoCestos = "./dados/" + nomeArquivo + "/" + nomeArquivo + ".hash";

        File dir = new File("./dados/" + nomeArquivo);
        if (!dir.exists()) dir.mkdirs();

        arqDiretorio = new RandomAccessFile(this.nomeArquivoDiretorio, "rw");
        arqCestos = new RandomAccessFile(this.nomeArquivoCestos, "rw");

        if (arqDiretorio.length() == 0) {
            profundidadeGlobal = 0;
            arqDiretorio.writeInt(profundidadeGlobal);

            // Cria o cesto inicial
            long enderecoCesto = arqCestos.length();
            Cesto cestoInicial = new Cesto(0, capacidadeCesto);
            salvarCesto(enderecoCesto, cestoInicial);

            arqDiretorio.writeLong(enderecoCesto);
        } else {
            arqDiretorio.seek(0);
            profundidadeGlobal = arqDiretorio.readInt();
        }
    }

    private int hash(int id, int profundidade) {
        return id % (int) Math.pow(2, profundidade);
    }

    public void create(int id, long enderecoFisico) throws Exception {
        int indiceDir = hash(id, profundidadeGlobal);
        arqDiretorio.seek(4 + (indiceDir * 8L));
        long enderecoCesto = arqDiretorio.readLong();

        Cesto cesto = lerCesto(enderecoCesto);

        if (!cesto.isCheio()) {
            cesto.inserir(id, enderecoFisico);
            salvarCesto(enderecoCesto, cesto);
        } else {
            dividirCesto(indiceDir, enderecoCesto, cesto, id, enderecoFisico);
        }
    }

    public long read(int id) throws Exception {
        int indiceDir = hash(id, profundidadeGlobal);
        arqDiretorio.seek(4 + (indiceDir * 8L));
        long enderecoCesto = arqDiretorio.readLong();

        Cesto cesto = lerCesto(enderecoCesto);
        return cesto.buscar(id);
    }

    public boolean update(int id, long novoEndereco) throws Exception {
        int indiceDir = hash(id, profundidadeGlobal);
        arqDiretorio.seek(4 + (indiceDir * 8L));
        long enderecoCesto = arqDiretorio.readLong();

        Cesto cesto = lerCesto(enderecoCesto);
        boolean sucesso = cesto.atualizar(id, novoEndereco);
        if (sucesso) {
            salvarCesto(enderecoCesto, cesto);
        }
        return sucesso;
    }

    public boolean delete(int id) throws Exception {
        int indiceDir = hash(id, profundidadeGlobal);
        arqDiretorio.seek(4 + (indiceDir * 8L));
        long enderecoCesto = arqDiretorio.readLong();

        Cesto cesto = lerCesto(enderecoCesto);
        boolean sucesso = cesto.remover(id);
        if (sucesso) {
            salvarCesto(enderecoCesto, cesto);
        }
        return sucesso;
    }

    // ==========================================
    // LÓGICA DE DIVISÃO (SPLIT) E REDIRECIONAMENTO
    // ==========================================

    private void dividirCesto(int indiceDir, long enderecoCestoAntigo, Cesto cestoAntigo, int novoId, long novoEndereco) throws Exception {
        if (cestoAntigo.profundidadeLocal == profundidadeGlobal) {
            dobrarDiretorio();
        }

        int novaProfundidade = cestoAntigo.profundidadeLocal + 1;
        Cesto cesto0 = new Cesto(novaProfundidade, capacidadeCesto);
        Cesto cesto1 = new Cesto(novaProfundidade, capacidadeCesto);

        long endCesto0 = enderecoCestoAntigo; // Reaproveita o endereço do cesto antigo
        long endCesto1 = arqCestos.length();  // Novo cesto vai para o final do arquivo
        
        // Salva o cesto vazio para reservar espaço
        salvarCesto(endCesto1, cesto1);

        // Coleta todos os registros para redistribuir
        ArrayList<RegistroHash> todosRegistros = new ArrayList<>();
        for (int i = 0; i < cestoAntigo.quantidade; i++) {
            todosRegistros.add(new RegistroHash(cestoAntigo.ids[i], cestoAntigo.enderecos[i]));
        }
        todosRegistros.add(new RegistroHash(novoId, novoEndereco));

        // Redistribui com base no novo bit (nova profundidade)
        for (RegistroHash reg : todosRegistros) {
            int hashNovo = hash(reg.id, novaProfundidade);
            // O cesto 0 fica com os IDs cujo hash na nova profundidade corresponde ao índice original base
            if (hashNovo == hash(indiceDir, novaProfundidade)) {
                cesto0.inserir(reg.id, reg.endereco);
            } else {
                cesto1.inserir(reg.id, reg.endereco);
            }
        }

        salvarCesto(endCesto0, cesto0);
        salvarCesto(endCesto1, cesto1);

        atualizarDiretorio(novaProfundidade, indiceDir, endCesto0, endCesto1);
    }

    private void dobrarDiretorio() throws Exception {
        int tamanhoAntigo = (int) Math.pow(2, profundidadeGlobal);
        long[] ponteirosAntigos = new long[tamanhoAntigo];

        arqDiretorio.seek(4);
        for (int i = 0; i < tamanhoAntigo; i++) {
            ponteirosAntigos[i] = arqDiretorio.readLong();
        }

        profundidadeGlobal++;
        arqDiretorio.seek(0);
        arqDiretorio.writeInt(profundidadeGlobal);

        // Duplica os ponteiros (o índice 0 e o índice 0+tamanhoAntigo apontam para o mesmo cesto inicialmente)
        arqDiretorio.seek(4);
        for (int i = 0; i < tamanhoAntigo; i++) {
            arqDiretorio.writeLong(ponteirosAntigos[i]);
        }
        for (int i = 0; i < tamanhoAntigo; i++) {
            arqDiretorio.writeLong(ponteirosAntigos[i]);
        }
    }

    private void atualizarDiretorio(int novaProfundidade, int indiceBase, long endCesto0, long endCesto1) throws Exception {
        int totalDiretorios = (int) Math.pow(2, profundidadeGlobal);
        int salto = (int) Math.pow(2, novaProfundidade);
        int baseCesto0 = hash(indiceBase, novaProfundidade);
        int baseCesto1 = baseCesto0 + (int) Math.pow(2, novaProfundidade - 1);

        for (int i = baseCesto0; i < totalDiretorios; i += salto) {
            arqDiretorio.seek(4 + (i * 8L));
            arqDiretorio.writeLong(endCesto0);
        }

        for (int i = baseCesto1; i < totalDiretorios; i += salto) {
            arqDiretorio.seek(4 + (i * 8L));
            arqDiretorio.writeLong(endCesto1);
        }
    }

    // ==========================================
    // OPERAÇÕES DE I/O NO ARQUIVO DE CESTOS
    // ==========================================

    private Cesto lerCesto(long endereco) throws Exception {
        arqCestos.seek(endereco);
        int pLocal = arqCestos.readInt();
        int qtd = arqCestos.readInt();
        Cesto cesto = new Cesto(pLocal, capacidadeCesto);
        cesto.quantidade = qtd;

        for (int i = 0; i < capacidadeCesto; i++) {
            cesto.ids[i] = arqCestos.readInt();
            cesto.enderecos[i] = arqCestos.readLong();
        }
        return cesto;
    }

    private void salvarCesto(long endereco, Cesto cesto) throws Exception {
        arqCestos.seek(endereco);
        arqCestos.writeInt(cesto.profundidadeLocal);
        arqCestos.writeInt(cesto.quantidade);
        for (int i = 0; i < capacidadeCesto; i++) {
            arqCestos.writeInt(cesto.ids[i]);
            arqCestos.writeLong(cesto.enderecos[i]);
        }
    }

    // ==========================================
    // CLASSES INTERNAS AUXILIARES
    // ==========================================

    private class RegistroHash {
        int id;
        long endereco;
        RegistroHash(int id, long endereco) {
            this.id = id;
            this.endereco = endereco;
        }
    }

    private class Cesto {
        int profundidadeLocal;
        int quantidade;
        int[] ids;
        long[] enderecos;

        public Cesto(int profundidadeLocal, int capacidade) {
            this.profundidadeLocal = profundidadeLocal;
            this.quantidade = 0;
            this.ids = new int[capacidade];
            this.enderecos = new long[capacidade];
            for (int i = 0; i < capacidade; i++) {
                this.ids[i] = -1; // -1 indica espaço vazio
                this.enderecos[i] = -1;
            }
        }

        boolean isCheio() {
            return quantidade == ids.length;
        }

        void inserir(int id, long endereco) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == -1) {
                    ids[i] = id;
                    enderecos[i] = endereco;
                    quantidade++;
                    return;
                }
            }
        }

        long buscar(int id) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == id) return enderecos[i];
            }
            return -1;
        }

        boolean atualizar(int id, long novoEndereco) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == id) {
                    enderecos[i] = novoEndereco;
                    return true;
                }
            }
            return false;
        }

        boolean remover(int id) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == id) {
                    ids[i] = -1;
                    enderecos[i] = -1;
                    quantidade--;
                    return true;
                }
            }
            return false;
        }
    }
}