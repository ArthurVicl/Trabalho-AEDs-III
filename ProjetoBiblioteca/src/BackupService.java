import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de Backup e Restauração completo da aplicação.
 * 
 * Requisito Fase IV:
 * - Abrange todos os arquivos de dados (.db) e índices (.dir, .hash, .btree) em memória secundária.
 * - Gera um único arquivo compactado com LZW.
 * - Restaura integralmente os arquivos originais e índices para pleno funcionamento.
 */
public class BackupService {

    public static class ResultadoBackup {
        public long tamanhoOriginal;
        public long tamanhoCompactado;
        public double taxaCompressao;
        public int totalArquivos;
        public String caminhoArquivoBackup;

        public ResultadoBackup(long original, long compactado, double taxa, int total, String caminho) {
            this.tamanhoOriginal = original;
            this.tamanhoCompactado = compactado;
            this.taxaCompressao = taxa;
            this.totalArquivos = total;
            this.caminhoArquivoBackup = caminho;
        }

        @Override
        public String toString() {
            return String.format(
                "Backup gerado com sucesso via LZW!\n\n" +
                "Arquivo gerado: %s\n" +
                "Arquivos incluídos: %d\n" +
                "Tamanho original dos arquivos: %,d bytes\n" +
                "Tamanho do arquivo compactado: %,d bytes\n" +
                "Taxa de compressão: %.2f%%",
                caminhoArquivoBackup, totalArquivos, tamanhoOriginal, tamanhoCompactado, taxaCompressao
            );
        }
    }

    /**
     * Empacota todos os arquivos da pasta de dados e compacta em um único arquivo LZW.
     */
    public static ResultadoBackup criarBackup(File pastaDados, File arquivoDestino) throws Exception {
        if (!pastaDados.exists() || !pastaDados.isDirectory()) {
            throw new IOException("Diretório de dados não encontrado: " + pastaDados.getAbsolutePath());
        }

        List<File> arquivos = new ArrayList<>();
        listarArquivosRecursivo(pastaDados, arquivos);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        long tamanhoOriginalTotal = 0;
        String caminhoBase = pastaDados.getCanonicalPath();

        for (File f : arquivos) {
            String caminhoAbs = f.getCanonicalPath();
            String relativo = caminhoAbs.substring(caminhoBase.length());
            if (relativo.startsWith(File.separator)) {
                relativo = relativo.substring(1);
            }
            relativo = relativo.replace('\\', '/');

            byte[] conteudo = lerBytesArquivo(f);
            tamanhoOriginalTotal += conteudo.length;

            dos.writeUTF(relativo);
            dos.writeInt(conteudo.length);
            dos.write(conteudo);
        }

        // Marcador de fim de arquivo
        dos.writeUTF("");
        dos.flush();

        byte[] dadosEmpacotados = baos.toByteArray();

        // Compacta com LZW
        byte[] dadosCompactados = LZW.comprimir(dadosEmpacotados);

        // Salva no arquivo de destino
        if (arquivoDestino.getParentFile() != null) {
            arquivoDestino.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(arquivoDestino)) {
            fos.write(dadosCompactados);
        }

        long tamanhoCompactado = arquivoDestino.length();
        double taxa = 0.0;
        if (tamanhoOriginalTotal > 0) {
            taxa = (1.0 - ((double) tamanhoCompactado / (double) tamanhoOriginalTotal)) * 100.0;
        }

        return new ResultadoBackup(tamanhoOriginalTotal, tamanhoCompactado, taxa, arquivos.size(), arquivoDestino.getName());
    }

    /**
     * Descompacta o arquivo LZW e recria a pasta de dados com todos os seus subdiretórios e arquivos.
     */
    public static int restaurarBackup(File arquivoBackup, File pastaDestinoDados) throws Exception {
        if (!arquivoBackup.exists() || !arquivoBackup.isFile()) {
            throw new IOException("Arquivo de backup não encontrado: " + arquivoBackup.getAbsolutePath());
        }

        byte[] bytesCompactados = lerBytesArquivo(arquivoBackup);

        // Descompacta com LZW
        byte[] dadosDescompactados = LZW.descomprimir(bytesCompactados);

        ByteArrayInputStream bais = new ByteArrayInputStream(dadosDescompactados);
        DataInputStream dis = new DataInputStream(bais);

        int restaurados = 0;
        while (true) {
            String relativo = dis.readUTF();
            if (relativo == null || relativo.isEmpty()) {
                break;
            }

            int tamanho = dis.readInt();
            byte[] conteudo = new byte[tamanho];
            dis.readFully(conteudo);

            File destFile = new File(pastaDestinoDados, relativo);
            if (destFile.getParentFile() != null) {
                destFile.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                fos.write(conteudo);
            }
            restaurados++;
        }

        return restaurados;
    }

    private static void listarArquivosRecursivo(File dir, List<File> resultado) {
        File[] lista = dir.listFiles();
        if (lista != null) {
            for (File f : lista) {
                if (f.isDirectory()) {
                    listarArquivosRecursivo(f, resultado);
                } else if (f.isFile()) {
                    resultado.add(f);
                }
            }
        }
    }

    private static byte[] lerBytesArquivo(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] b = new byte[(int) f.length()];
            int lidos = 0;
            while (lidos < b.length) {
                int r = fis.read(b, lidos, b.length - lidos);
                if (r == -1) break;
                lidos += r;
            }
            return b;
        }
    }
}
