import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação do algoritmo de compactação e descompactação LZW (Lempel-Ziv-Welch).
 * 
 * Requisito Fase IV: Desenvolvido pelo grupo para gerar backup completo e restauração
 * dos arquivos da base de dados e índices.
 */
public class LZW {

    private static final int MAX_CODIGOS = 4096; // 12 bits = 0 a 4095
    private static final int CODIGOS_INICIAIS = 256; // Bytes 0x00 a 0xFF

    /**
     * Compacta uma sequência de bytes utilizando LZW com códigos de 12 bits.
     * 
     * @param entrada Array de bytes com o conteúdo original
     * @return Array de bytes compactado
     * @throws IOException
     */
    public static byte[] comprimir(byte[] entrada) throws IOException {
        if (entrada == null || entrada.length == 0) {
            return new byte[0];
        }

        // Tabela de símbolos: prefixCode (24 bits) + byte (8 bits) -> código inteiro
        Map<Long, Integer> dicionario = new HashMap<>();
        int proximoCodigo = CODIGOS_INICIAIS;

        List<Integer> codigos = new ArrayList<>();
        int p = -1; // Código do prefixo atual

        for (int i = 0; i < entrada.length; i++) {
            int b = entrada[i] & 0xFF;
            if (p == -1) {
                p = b;
            } else {
                long chave = ((long) p << 8) | b;
                if (dicionario.containsKey(chave)) {
                    p = dicionario.get(chave);
                } else {
                    codigos.add(p);
                    if (proximoCodigo < MAX_CODIGOS) {
                        dicionario.put(chave, proximoCodigo++);
                    }
                    p = b;
                }
            }
        }

        if (p != -1) {
            codigos.add(p);
        }

        return empacotar12Bits(codigos);
    }

    /**
     * Descompacta uma sequência de bytes gerada pelo LZW.
     * 
     * @param compactado Array de bytes compactado
     * @return Array de bytes original restaurado
     * @throws IOException
     */
    public static byte[] descomprimir(byte[] compactado) throws IOException {
        if (compactado == null || compactado.length == 0) {
            return new byte[0];
        }

        List<Integer> codigos = desempacotar12Bits(compactado);
        if (codigos.isEmpty()) {
            return new byte[0];
        }

        // Dicionário de descompressão: código -> sequência de bytes
        byte[][] dicionario = new byte[MAX_CODIGOS][];
        for (int i = 0; i < CODIGOS_INICIAIS; i++) {
            dicionario[i] = new byte[] { (byte) i };
        }
        int proximoCodigo = CODIGOS_INICIAIS;

        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        int codigoAntigo = codigos.get(0);
        saida.write(dicionario[codigoAntigo]);

        for (int i = 1; i < codigos.size(); i++) {
            int codigoAtual = codigos.get(i);
            byte[] entrada;

            if (codigoAtual < proximoCodigo) {
                entrada = dicionario[codigoAtual];
            } else if (codigoAtual == proximoCodigo) {
                // Caso especial LZW (KwKwK): sequência anterior + primeiro byte da sequência anterior
                byte[] cadeiaAntiga = dicionario[codigoAntigo];
                entrada = new byte[cadeiaAntiga.length + 1];
                System.arraycopy(cadeiaAntiga, 0, entrada, 0, cadeiaAntiga.length);
                entrada[cadeiaAntiga.length] = cadeiaAntiga[0];
            } else {
                throw new IOException("Código LZW inválido na descompressão: " + codigoAtual);
            }

            saida.write(entrada);

            // Adiciona nova entrada no dicionário
            if (proximoCodigo < MAX_CODIGOS) {
                byte[] cadeiaAntiga = dicionario[codigoAntigo];
                byte[] novaEntrada = new byte[cadeiaAntiga.length + 1];
                System.arraycopy(cadeiaAntiga, 0, novaEntrada, 0, cadeiaAntiga.length);
                novaEntrada[cadeiaAntiga.length] = entrada[0];
                dicionario[proximoCodigo++] = novaEntrada;
            }

            codigoAntigo = codigoAtual;
        }

        return saida.toByteArray();
    }

    /**
     * Empacota uma lista de inteiros de 12 bits em um array de bytes contínuo.
     * Cada par de códigos (24 bits) ocupa exatamente 3 bytes.
     */
    private static byte[] empacotar12Bits(List<Integer> codigos) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Armazena a contagem exata de códigos para desempacotamento exato
        dos.writeInt(codigos.size());

        int buffer = 0;
        int bitsNoBuffer = 0;

        for (int codigo : codigos) {
            buffer = (buffer << 12) | (codigo & 0xFFF);
            bitsNoBuffer += 12;

            while (bitsNoBuffer >= 8) {
                bitsNoBuffer -= 8;
                baos.write((buffer >> bitsNoBuffer) & 0xFF);
            }
        }

        if (bitsNoBuffer > 0) {
            baos.write((buffer << (8 - bitsNoBuffer)) & 0xFF);
        }

        return baos.toByteArray();
    }

    /**
     * Desempacota um array de bytes em uma lista de inteiros de 12 bits.
     */
    private static List<Integer> desempacotar12Bits(byte[] compactado) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compactado);
        DataInputStream dis = new DataInputStream(bais);

        int totalCodigos = dis.readInt();
        List<Integer> codigos = new ArrayList<>(totalCodigos);

        int buffer = 0;
        int bitsNoBuffer = 0;

        for (int i = 0; i < totalCodigos; i++) {
            while (bitsNoBuffer < 12) {
                int b = bais.read();
                if (b == -1) {
                    throw new IOException("Fim inesperado dos dados ao desempacotar LZW.");
                }
                buffer = (buffer << 8) | (b & 0xFF);
                bitsNoBuffer += 8;
            }

            bitsNoBuffer -= 12;
            int codigo = (buffer >> bitsNoBuffer) & 0xFFF;
            codigos.add(codigo);
        }

        return codigos;
    }
}
