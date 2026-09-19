import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdenacaoExternaUsuario {

    // Define o número de registos carregados para a RAM de cada vez.
    // Utilize um valor baixo (ex: 10) para forçar a geração de múltiplos
    // ficheiros temporários e testar a intercalação com poucos registos.
    private static final int TAMANHO_BLOCO = 10; 
    
    private String arquivoOrigem = "./dados/usuarios/usuarios.db";
    private String diretorioTemp = "./dados/usuarios/temp/";
    private String arquivoDestino = "./dados/usuarios/usuarios_ordenados.db";

    public void ordenarPorNome() throws Exception {
        File dirTemp = new File(diretorioTemp);
        if (!dirTemp.exists()) dirTemp.mkdirs();

        int numArquivosTemp = distribuir();

        if (numArquivosTemp == 0) {
            System.out.println("Não há registos suficientes para ordenar.");
            return;
        }

        intercalar(numArquivosTemp);
    }

    // ==========================================
    // FASE 1: DISTRIBUIÇÃO
    // ==========================================
    private int distribuir() throws Exception {
        RandomAccessFile arqOrigem = new RandomAccessFile(arquivoOrigem, "r");
        
        // Pula o cabeçalho (4 bytes do último ID + 8 bytes do ponteiro de excluídos)
        if (arqOrigem.length() >= 12) {
            arqOrigem.seek(12); 
        }

        List<Usuario> bloco = new ArrayList<>();
        int numArquivosTemp = 0;

        while (arqOrigem.getFilePointer() < arqOrigem.length()) {
            byte lapide = arqOrigem.readByte();
            short tamanho = arqOrigem.readShort();
            byte[] bytes = new byte[tamanho];
            arqOrigem.read(bytes);

            // Apenas carrega os registos que não estão excluídos logicamente
            if (lapide == ' ') {
                Usuario u = new Usuario();
                u.fromByteArray(bytes);
                bloco.add(u);
            }

            // Se o bloco atingiu o limite ou chegámos ao fim do ficheiro
            if (bloco.size() == TAMANHO_BLOCO || arqOrigem.getFilePointer() == arqOrigem.length()) {
                if (!bloco.isEmpty()) {
                    // Ordena o bloco em memória primária pelo atributo Nome
                    bloco.sort(Comparator.comparing(Usuario::getNome));

                    // Grava o bloco ordenado num ficheiro temporário
                    RandomAccessFile arqTemp = new RandomAccessFile(diretorioTemp + "temp_" + numArquivosTemp + ".tmp", "rw");
                    for (Usuario u : bloco) {
                        byte[] b = u.toByteArray();
                        arqTemp.writeShort(b.length);
                        arqTemp.write(b);
                    }
                    arqTemp.close();
                    
                    bloco.clear();
                    numArquivosTemp++;
                }
            }
        }
        arqOrigem.close();
        return numArquivosTemp;
    }

    // ==========================================
    // FASE 2: INTERCALAÇÃO BALANCEADA (MERGE)
    // ==========================================
    private void intercalar(int numArquivosTemp) throws Exception {
        RandomAccessFile[] arqsTemp = new RandomAccessFile[numArquivosTemp];
        Usuario[] usuariosAtuais = new Usuario[numArquivosTemp];

        // Abre todos os ficheiros temporários em simultâneo e lê o primeiro registo de cada um
        for (int i = 0; i < numArquivosTemp; i++) {
            arqsTemp[i] = new RandomAccessFile(diretorioTemp + "temp_" + i + ".tmp", "r");
            usuariosAtuais[i] = lerProximoUsuario(arqsTemp[i]);
        }

        RandomAccessFile arqFinal = new RandomAccessFile(arquivoDestino, "rw");
        arqFinal.setLength(0); // Limpa o ficheiro final se ele já existir

        while (true) {
            int indiceMenor = -1;
            Usuario menorUsuario = null;

            // Percorre os utilizadores carregados para encontrar aquele com o menor nome (ordem alfabética)
            for (int i = 0; i < numArquivosTemp; i++) {
                if (usuariosAtuais[i] != null) {
                    if (menorUsuario == null || usuariosAtuais[i].getNome().compareToIgnoreCase(menorUsuario.getNome()) < 0) {
                        menorUsuario = usuariosAtuais[i];
                        indiceMenor = i;
                    }
                }
            }

            // Se todos os ficheiros temporários foram lidos e estão vazios
            if (indiceMenor == -1) {
                break; 
            }

            // Grava o registo vencedor no ficheiro ordenado
            byte[] b = menorUsuario.toByteArray();
            arqFinal.writeShort(b.length);
            arqFinal.write(b);

            // Carrega o próximo registo do ficheiro temporário que acabou de "vencer"
            usuariosAtuais[indiceMenor] = lerProximoUsuario(arqsTemp[indiceMenor]);
        }

        arqFinal.close();

        // Fecha e elimina os ficheiros temporários do disco
        for (int i = 0; i < numArquivosTemp; i++) {
            arqsTemp[i].close();
            new File(diretorioTemp + "temp_" + i + ".tmp").delete();
        }

        System.out.println("Ordenação Externa concluída com sucesso!");
        imprimirResultado();
    }

    private Usuario lerProximoUsuario(RandomAccessFile arq) throws Exception {
        if (arq.getFilePointer() < arq.length()) {
            short tamanho = arq.readShort();
            byte[] bytes = new byte[tamanho];
            arq.read(bytes);
            Usuario u = new Usuario();
            u.fromByteArray(bytes);
            return u;
        }
        return null;
    }

    private void imprimirResultado() throws Exception {
        RandomAccessFile arq = new RandomAccessFile(arquivoDestino, "r");
        System.out.println("\n--- RESULTADO DA ORDENAÇÃO EXTERNA (USUÁRIOS POR NOME) ---");
        
        while (arq.getFilePointer() < arq.length()) {
            short tamanho = arq.readShort();
            byte[] bytes = new byte[tamanho];
            arq.read(bytes);
            Usuario u = new Usuario();
            u.fromByteArray(bytes);
            System.out.println("Nome: " + u.getNome() + " | CPF: " + u.getCpf());
        }
        
        arq.close();
        System.out.println("----------------------------------------------------------\n");
    }
}