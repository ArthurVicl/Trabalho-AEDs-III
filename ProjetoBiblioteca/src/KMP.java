import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do algoritmo Knuth-Morris-Pratt (KMP) para casamento de padrões.
 * 
 * Requisito Fase IV: Desenvolvido do zero pelo grupo, sem utilizar funções
 * prontas da linguagem (como String.contains, indexOf, etc.).
 */
public class KMP {

    /**
     * Pré-processamento do padrão para calcular a tabela de prefixos (LPS - Longest Proper Prefix which is also Suffix).
     * Complexidade de tempo: O(m), onde m é o tamanho do padrão.
     * Complexidade de espaço: O(m).
     */
    public static int[] computarLPS(String padrao) {
        int m = padrao.length();
        int[] lps = new int[m];
        int len = 0; // comprimento do prefixo anterior mais longo
        int i = 1;
        lps[0] = 0; // lps[0] é sempre 0

        while (i < m) {
            if (padrao.charAt(i) == padrao.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    // Volta para o prefixo anterior
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Busca a primeira ocorrência do padrão no texto utilizando o algoritmo KMP.
     * Não utiliza nenhum método pronto como indexOf ou contains.
     * 
     * @param texto O texto onde será feita a busca
     * @param padrao O padrão a ser localizado
     * @return O índice da primeira ocorrência (>= 0) ou -1 caso não encontrado
     */
    public static int buscar(String texto, String padrao) {
        if (padrao == null || padrao.isEmpty()) return 0;
        if (texto == null || texto.isEmpty()) return -1;

        // Normalização manual para busca sem distinção de maiúsculas/minúsculas
        String t = toLower(texto);
        String p = toLower(padrao);

        int n = t.length();
        int m = p.length();
        if (m > n) return -1;

        int[] lps = computarLPS(p);

        int i = 0; // ponteiro no texto
        int j = 0; // ponteiro no padrão

        while (i < n) {
            if (p.charAt(j) == t.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                // Padrão completo encontrado
                return i - j;
            } else if (i < n && p.charAt(j) != t.charAt(i)) {
                if (j != 0) {
                    // Usa a tabela LPS para não retroceder no texto (i não diminui)
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1; // Não encontrado
    }

    /**
     * Retorna se o padrão está contido no texto.
     */
    public static boolean contem(String texto, String padrao) {
        return buscar(texto, padrao) != -1;
    }

    /**
     * Retorna todas as posições em que o padrão ocorre no texto.
     */
    public static List<Integer> buscarTodasOcorrencias(String texto, String padrao) {
        List<Integer> ocorrencias = new ArrayList<>();
        if (padrao == null || padrao.isEmpty() || texto == null || texto.isEmpty()) {
            return ocorrencias;
        }

        String t = toLower(texto);
        String p = toLower(padrao);

        int n = t.length();
        int m = p.length();
        if (m > n) return ocorrencias;

        int[] lps = computarLPS(p);

        int i = 0;
        int j = 0;

        while (i < n) {
            if (p.charAt(j) == t.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                ocorrencias.add(i - j);
                j = lps[j - 1];
            } else if (i < n && p.charAt(j) != t.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return ocorrencias;
    }

    /**
     * Converte para minúsculas manualmente char a char, garantindo independência.
     */
    private static String toLower(String s) {
        if (s == null) return null;
        char[] chars = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            chars[i] = Character.toLowerCase(s.charAt(i));
        }
        return new String(chars);
    }
}
