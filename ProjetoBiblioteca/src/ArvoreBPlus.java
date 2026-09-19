import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ArvoreBPlus {

    private String nomeArquivo;
    private RandomAccessFile arquivo;
    private int ordem;
    private long enderecoRaiz;

    public ArvoreBPlus(String nome, int ordem) throws Exception {
        this.ordem = ordem;
        this.nomeArquivo = "./dados/" + nome + "/" + nome + ".btree";

        File dir = new File("./dados/" + nome);
        if (!dir.exists()) dir.mkdirs();

        arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

        if (arquivo.length() < 8) {
            // Inicializa a árvore com uma raiz vazia (que é uma folha)
            enderecoRaiz = 8;
            arquivo.writeLong(enderecoRaiz);
            NoBPlus raiz = new NoBPlus(true);
            salvarNo(enderecoRaiz, raiz);
        } else {
            arquivo.seek(0);
            enderecoRaiz = arquivo.readLong();
        }
    }
    public List<Integer> read(int idAutor) throws Exception {
        List<Integer> livros = new ArrayList<>();
        long enderecoAtual = enderecoRaiz;
        NoBPlus no = lerNo(enderecoAtual);

        // 1. Desce na árvore até encontrar a folha correta
        while (!no.folha) {
            int i = 0;
            // Procura o primeiro filho onde a chave armazenada seja maior ou igual ao idAutor
            while (i < no.numElementos && no.elementos[i].chave < idAutor) {
                i++;
            }
            enderecoAtual = no.filhos[i];
            no = lerNo(enderecoAtual);
        }

        // 2. Varredura sequencial nas folhas 
        boolean buscaConcluida = false;
        while (!buscaConcluida && no != null) {
            for (int i = 0; i < no.numElementos; i++) {
                if (no.elementos[i].chave == idAutor) {
                    livros.add(no.elementos[i].valor); // Adiciona o idLivro à lista
                } else if (no.elementos[i].chave > idAutor) {
                    buscaConcluida = true;
                    break;
                }
            }

            // Se não concluiu a busca e há uma próxima folha, segue o ponteiro
            if (!buscaConcluida && no.proximaFolha != -1) {
                no = lerNo(no.proximaFolha);
            } else {
                buscaConcluida = true;
            }
        }

        return livros;
    }

    // ==========================================
    // INSERÇÃO COM SPLIT (DIVISÃO DE NÓS)
    // ==========================================
    public void create(int idAutor, int idLivro) throws Exception {
        Elemento novoElemento = new Elemento(idAutor, idLivro);
        RetornoInsercao retorno = inserirRecursivo(enderecoRaiz, novoElemento);

        // Se a raiz sofreu split, a árvore cresce um nível para cima
        if (retorno != null) {
            NoBPlus novaRaiz = new NoBPlus(false);
            novaRaiz.elementos[0] = retorno.elementoPromovido;
            novaRaiz.filhos[0] = enderecoRaiz;
            novaRaiz.filhos[1] = retorno.filhoDireito;
            novaRaiz.numElementos = 1;

            long novoEnderecoRaiz = arquivo.length();
            salvarNo(novoEnderecoRaiz, novaRaiz);

            enderecoRaiz = novoEnderecoRaiz;
            arquivo.seek(0);
            arquivo.writeLong(enderecoRaiz);
        }
    }

    private class RetornoInsercao {
        Elemento elementoPromovido;
        long filhoDireito;

        public RetornoInsercao(Elemento elemento, long filho) {
            this.elementoPromovido = elemento;
            this.filhoDireito = filho;
        }
    }

    private RetornoInsercao inserirRecursivo(long enderecoNo, Elemento novoElemento) throws Exception {
        NoBPlus no = lerNo(enderecoNo);

        if (no.folha) {
            // Caso 1: Há espaço na folha
            if (no.numElementos < ordem - 1) {
                inserirNoVetor(no, novoElemento, -1);
                salvarNo(enderecoNo, no);
                return null;
            } else {
                // Caso 2: Overflow na folha (Split)
                return dividirFolha(enderecoNo, no, novoElemento);
            }
        } else {
            // Encontra o ponteiro correto para descer
            int i = 0;
            while (i < no.numElementos && compara(novoElemento, no.elementos[i]) >= 0) {
                i++;
            }

            RetornoInsercao retornoFilho = inserirRecursivo(no.filhos[i], novoElemento);

            if (retornoFilho == null) {
                return null; // Filho não sofreu split
            }

            // O filho sofreu split, o elemento mediano sobe para este nó
            if (no.numElementos < ordem - 1) {
                inserirNoVetor(no, retornoFilho.elementoPromovido, retornoFilho.filhoDireito);
                salvarNo(enderecoNo, no);
                return null;
            } else {
                // Split do nó interno
                return dividirNoInterno(enderecoNo, no, retornoFilho.elementoPromovido, retornoFilho.filhoDireito);
            }
        }
    }

    private void inserirNoVetor(NoBPlus no, Elemento elemento, long filhoDireito) {
        int i = no.numElementos - 1;
        while (i >= 0 && compara(elemento, no.elementos[i]) < 0) {
            no.elementos[i + 1] = no.elementos[i];
            if (!no.folha) {
                no.filhos[i + 2] = no.filhos[i + 1];
            }
            i--;
        }
        no.elementos[i + 1] = elemento;
        if (!no.folha) {
            no.filhos[i + 2] = filhoDireito;
        }
        no.numElementos++;
    }

    private RetornoInsercao dividirFolha(long enderecoNo, NoBPlus no, Elemento novoElemento) throws Exception {
        Elemento[] temp = new Elemento[ordem];
        for (int i = 0; i < no.numElementos; i++) temp[i] = no.elementos[i];
        
        int i = ordem - 2;
        while (i >= 0 && compara(novoElemento, temp[i]) < 0) {
            temp[i + 1] = temp[i];
            i--;
        }
        temp[i + 1] = novoElemento;

        int meio = ordem / 2;
        NoBPlus novoNoDireito = new NoBPlus(true);
        novoNoDireito.numElementos = ordem - meio;
        no.numElementos = meio;

        for (int j = 0; j < novoNoDireito.numElementos; j++) {
            novoNoDireito.elementos[j] = temp[meio + j];
        }
        for (int j = 0; j < no.numElementos; j++) {
            no.elementos[j] = temp[j];
        }
        for(int j = no.numElementos; j < ordem - 1; j++) {
            no.elementos[j] = new Elemento(-1, -1);
        }

        // Mantém a lista encadeada das folhas
        novoNoDireito.proximaFolha = no.proximaFolha;
        long enderecoNovoNo = arquivo.length();
        no.proximaFolha = enderecoNovoNo;

        salvarNo(enderecoNo, no);
        salvarNo(enderecoNovoNo, novoNoDireito);

        return new RetornoInsercao(novoNoDireito.elementos[0], enderecoNovoNo);
    }

    private RetornoInsercao dividirNoInterno(long enderecoNo, NoBPlus no, Elemento elementoPromovido, long novoFilhoDireito) throws Exception {
        Elemento[] tempElementos = new Elemento[ordem];
        long[] tempFilhos = new long[ordem + 1];
        
        for (int i = 0; i < no.numElementos; i++) {
            tempElementos[i] = no.elementos[i];
            tempFilhos[i] = no.filhos[i];
        }
        tempFilhos[no.numElementos] = no.filhos[no.numElementos];

        int i = ordem - 2;
        while (i >= 0 && compara(elementoPromovido, tempElementos[i]) < 0) {
            tempElementos[i + 1] = tempElementos[i];
            tempFilhos[i + 2] = tempFilhos[i + 1];
            i--;
        }
        tempElementos[i + 1] = elementoPromovido;
        tempFilhos[i + 2] = novoFilhoDireito;

        int meio = ordem / 2;
        Elemento subir = tempElementos[meio];

        NoBPlus novoNoDireito = new NoBPlus(false);
        novoNoDireito.numElementos = ordem - 1 - meio;
        no.numElementos = meio;

        for (int j = 0; j < novoNoDireito.numElementos; j++) {
            novoNoDireito.elementos[j] = tempElementos[meio + 1 + j];
        }
        for (int j = 0; j <= novoNoDireito.numElementos; j++) {
            novoNoDireito.filhos[j] = tempFilhos[meio + 1 + j];
        }
        for (int j = 0; j < no.numElementos; j++) {
            no.elementos[j] = tempElementos[j];
        }
        for(int j = no.numElementos; j < ordem - 1; j++) no.elementos[j] = new Elemento(-1, -1);
        for(int j = no.numElementos + 1; j < ordem; j++) no.filhos[j] = -1;

        long enderecoNovoNo = arquivo.length();
        salvarNo(enderecoNo, no);
        salvarNo(enderecoNovoNo, novoNoDireito);

        return new RetornoInsercao(subir, enderecoNovoNo);
    }

    private int compara(Elemento e1, Elemento e2) {
        if (e1.chave != e2.chave) return Integer.compare(e1.chave, e2.chave);
        return Integer.compare(e1.valor, e2.valor);
    }

    // ==========================================
    // EXCLUSÃO
    // ==========================================
    public void delete(int idAutor, int idLivro) throws Exception {
        Elemento elementoBuscado = new Elemento(idAutor, idLivro);
        excluirRecursivo(enderecoRaiz, elementoBuscado);
    }

    private boolean excluirRecursivo(long enderecoNo, Elemento elemento) throws Exception {
        NoBPlus no = lerNo(enderecoNo);

        if (no.folha) {
            int i = 0;
            while (i < no.numElementos && compara(elemento, no.elementos[i]) > 0) i++;
            
            // Remove da folha e reajusta o bloco
            if (i < no.numElementos && compara(elemento, no.elementos[i]) == 0) {
                for (int j = i; j < no.numElementos - 1; j++) no.elementos[j] = no.elementos[j + 1];
                no.elementos[no.numElementos - 1] = new Elemento(-1, -1);
                no.numElementos--;
                salvarNo(enderecoNo, no);
                return true;
            }
            return false;
        } else {
            int i = 0;
            while (i < no.numElementos && compara(elemento, no.elementos[i]) >= 0) i++;
            return excluirRecursivo(no.filhos[i], elemento);
        }
    }

    private NoBPlus lerNo(long endereco) throws Exception {
        arquivo.seek(endereco);
        boolean folha = arquivo.readBoolean();
        int numElementos = arquivo.readInt();
        
        NoBPlus no = new NoBPlus(folha);
        no.numElementos = numElementos;

        for (int i = 0; i < ordem - 1; i++) {
            int chave = arquivo.readInt();
            int valor = arquivo.readInt();
            no.elementos[i] = new Elemento(chave, valor);
        }

        if (!folha) {
            for (int i = 0; i < ordem; i++) {
                no.filhos[i] = arquivo.readLong();
            }
        } else {
            no.proximaFolha = arquivo.readLong();
        }

        return no;
    }

    private void salvarNo(long endereco, NoBPlus no) throws Exception {
        arquivo.seek(endereco);
        arquivo.writeBoolean(no.folha);
        arquivo.writeInt(no.numElementos);

        for (int i = 0; i < ordem - 1; i++) {
            if (i < no.numElementos) {
                arquivo.writeInt(no.elementos[i].chave);
                arquivo.writeInt(no.elementos[i].valor);
            } else {
                arquivo.writeInt(-1);
                arquivo.writeInt(-1);
            }
        }

        if (!no.folha) {
            for (int i = 0; i < ordem; i++) {
                if (i <= no.numElementos) {
                    arquivo.writeLong(no.filhos[i]);
                } else {
                    arquivo.writeLong(-1);
                }
            }
        } else {
            arquivo.writeLong(no.proximaFolha);
        }
    }

    private class Elemento {
        int chave; // Representa a Chave Estrangeira (ex: idAutor)
        int valor; // Representa a Chave Primária (ex: idLivro)

        public Elemento(int chave, int valor) {
            this.chave = chave;
            this.valor = valor;
        }
    }

    private class NoBPlus {
        boolean folha;
        int numElementos;
        Elemento[] elementos;
        long[] filhos;
        long proximaFolha;

        public NoBPlus(boolean folha) {
            this.folha = folha;
            this.numElementos = 0;
            this.elementos = new Elemento[ordem - 1];
            this.filhos = new long[ordem];
            this.proximaFolha = -1;
            
            for(int i = 0; i < ordem - 1; i++) {
                elementos[i] = new Elemento(-1, -1);
            }
            for(int i = 0; i < ordem; i++) {
                filhos[i] = -1;
            }
        }
    }
}