import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class TesteFaseIV {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("INICIANDO TESTES AUTOMATIZADOS - FASE IV");
        System.out.println("==================================================");

        try {
            // -----------------------------------------------------------------
            // TESTE 1: Algoritmo KMP (Casamento de Padrões)
            // -----------------------------------------------------------------
            System.out.println("\n[1] TESTANDO ALGORITMO KMP...");
            String texto1 = "O Senhor dos Anéis: A Sociedade do Anel";
            String padrao1 = "Senhor";
            int pos1 = KMP.buscar(texto1, padrao1);
            System.out.println("  Texto: \"" + texto1 + "\"");
            System.out.println("  Padrão: \"" + padrao1 + "\" -> Encontrado no índice: " + pos1);
            if (pos1 != 2) throw new RuntimeException("Erro KMP 1: esperado 2, obtido " + pos1);

            String padrao2 = "pedra";
            String texto2 = "Harry Potter e a Pedra Filosofal";
            int pos2 = KMP.buscar(texto2, padrao2);
            System.out.println("  Texto: \"" + texto2 + "\"");
            System.out.println("  Padrão: \"" + padrao2 + "\" -> Encontrado no índice: " + pos2);
            if (pos2 != 17) throw new RuntimeException("Erro KMP 2: esperado 17, obtido " + pos2);

            String padrao3 = "Inexistente";
            int pos3 = KMP.buscar(texto1, padrao3);
            System.out.println("  Padrão inexistente -> Retorno: " + pos3);
            if (pos3 != -1) throw new RuntimeException("Erro KMP 3: esperado -1, obtido " + pos3);

            System.out.println("  => TESTES DO KMP APROVADOS COM SUCESSO!");

            // -----------------------------------------------------------------
            // TESTE 2: Algoritmo LZW e Backup / Restauração
            // -----------------------------------------------------------------
            System.out.println("\n[2] TESTANDO COMPACTAÇÃO E RESTAURAÇÃO LZW...");
            File pastaDados = new File("./dados");
            File arquivoBackup = new File("./backup_teste.lzw");
            File pastaRestaurada = new File("./dados_teste_restaurado");

            if (!pastaDados.exists()) {
                System.out.println("  Criando dados com PopulateDB...");
                PopulateDB.main(new String[0]);
            }

            BackupService.ResultadoBackup res = BackupService.criarBackup(pastaDados, arquivoBackup);
            System.out.println("  " + res.toString().replace("\n", "\n  "));

            if (res.tamanhoCompactado >= res.tamanhoOriginal) {
                System.out.println("  AVISO: Tamanho compactado não é menor (base muito pequena)");
            } else {
                System.out.println("  => Taxa de compressão positiva: " + String.format("%.2f%%", res.taxaCompressao));
            }

            // Testando restauração
            int restaurados = BackupService.restaurarBackup(arquivoBackup, pastaRestaurada);
            System.out.println("  Arquivos restaurados para teste: " + restaurados);

            // Comparando integridade byte a byte de todos os arquivos
            verificarIntegridade(pastaDados, pastaRestaurada);
            System.out.println("  => INTEGRIDADE 100% CONFIRMADA! Todos os bytes conferem perfeitamente.");

            // Limpando arquivos de teste temporários
            arquivoBackup.delete();
            apagarDiretorio(pastaRestaurada);

            // -----------------------------------------------------------------
            // TESTE 3: Busca KMP integrada ao LivroDAO
            // -----------------------------------------------------------------
            System.out.println("\n[3] TESTANDO BUSCA KMP INTEGRADA AO LivroDAO...");
            LivroDAO livroDAO = new LivroDAO();
            List<Livro> livrosEncontrados = livroDAO.buscarPorTituloKMP("Senhor");
            System.out.println("  Livros encontrados com 'Senhor': " + livrosEncontrados.size());
            for (Livro l : livrosEncontrados) {
                System.out.println("    - ID " + l.getId() + ": " + l.getTitulo());
            }
            if (livrosEncontrados.isEmpty()) {
                System.out.println("  (Nenhum livro retornado, base vazia ou sem o termo)");
            } else {
                System.out.println("  => BUSCA KMP NO BANCO FUNCIONANDO PERFEITAMENTE!");
            }
            livroDAO.close();

            System.out.println("\n==================================================");
            System.out.println("TODOS OS TESTES DA FASE IV PASSARAM COM SUCESSO!");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("ERRO NOS TESTES: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void verificarIntegridade(File original, File restaurado) throws Exception {
        if (original.isDirectory()) {
            File[] files = original.listFiles();
            if (files != null) {
                for (File f : files) {
                    File r = new File(restaurado, f.getName());
                    verificarIntegridade(f, r);
                }
            }
        } else if (original.isFile()) {
            if (!restaurado.exists()) {
                throw new RuntimeException("Arquivo não encontrado na restauração: " + restaurado.getAbsolutePath());
            }
            byte[] bOrig = Files.readAllBytes(original.toPath());
            byte[] bRest = Files.readAllBytes(restaurado.toPath());
            if (!Arrays.equals(bOrig, bRest)) {
                throw new RuntimeException("Inconsistência de bytes no arquivo: " + original.getName());
            }
        }
    }

    private static void apagarDiretorio(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) apagarDiretorio(f);
                f.delete();
            }
        }
        dir.delete();
    }
}
