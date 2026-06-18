import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Main <arquivo-de-entrada> [FIRST_FIT|BEST_FIT|WORST_FIT]");
            System.out.println("Veja o cabeçalho de Main.java para o formato do arquivo.");
            return;
        }

        String caminho = args[0];
        GerenciadorMemoria.Algoritmo algoritmoCLI = null;
        if (args.length >= 2) {
            algoritmoCLI = parseAlgoritmo(args[1]);
        }

        GerenciadorMemoria memoria = null;
        int passo = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }
                String[] t = linha.split("\\s+");
                String comando = t[0].toUpperCase();

                switch (comando) {
                    case "MEMORIA": {
                        int tamanho = Integer.parseInt(t[1]);
                        memoria = new GerenciadorMemoria(tamanho);
                        GerenciadorMemoria.Algoritmo alg = GerenciadorMemoria.Algoritmo.FIRST_FIT;
                        if (t.length >= 3) {
                            alg = parseAlgoritmo(t[2]);
                        }
                        if (algoritmoCLI != null) {
                            alg = algoritmoCLI;
                        }
                        memoria.setAlgoritmo(alg);
                        System.out.println("=== Memória inicializada: " + tamanho
                                + " bytes | algoritmo " + alg + " ===");
                        break;
                    }
                    case "ALOCAR": {
                        if (memoria == null) {
                            System.out.println("ERRO: defina MEMORIA antes de alocar.");
                            break;
                        }
                        String id = t[1];
                        int tamanho = Integer.parseInt(t[2]);
                        passo++;
                        System.out.println("\n--- Passo " + passo + ": ALOCAR " + id
                                + " (" + tamanho + " bytes) ---");

                        boolean fragAntes = memoria.haFragmentacaoExterna(tamanho);
                        int end = memoria.alocar(id, tamanho);

                        if (end >= 0) {
                            System.out.println("  OK: " + id + " alocado no endereço " + end);
                        } else if (fragAntes) {
                            System.out.println("  FALHA: fragmentação externa — há "
                                    + memoria.totalLivre() + " bytes livres no total, mas a maior "
                                    + "brecha contígua é de apenas " + memoria.maiorBrecha() + " bytes.");
                        } else {
                            System.out.println("  FALHA: espaço insuficiente — só há "
                                    + memoria.totalLivre() + " bytes livres.");
                        }
                        memoria.imprimirEstado();
                        break;
                    }
                    case "LIBERAR": {
                        if (memoria == null) {
                            System.out.println("ERRO: defina MEMORIA antes de liberar.");
                            break;
                        }
                        String id = t[1];
                        passo++;
                        System.out.println("\n--- Passo " + passo + ": LIBERAR " + id + " ---");
                        boolean ok = memoria.liberar(id);
                        System.out.println(ok
                                ? "  OK: memória de " + id + " liberada."
                                : "  Aviso: nenhum bloco do processo " + id + " foi encontrado.");
                        memoria.imprimirEstado();
                        break;
                    }
                    default:
                        System.out.println("Comando desconhecido ignorado: " + linha);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Erro de formato no arquivo de entrada: " + e.getMessage());
            return;
        }

        if (memoria != null) {
            imprimirRelatorio(memoria);
        }
    }

    private static GerenciadorMemoria.Algoritmo parseAlgoritmo(String s) {
        try {
            return GerenciadorMemoria.Algoritmo.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Algoritmo inválido '" + s + "', usando FIRST_FIT.");
            return GerenciadorMemoria.Algoritmo.FIRST_FIT;
        }
    }

    private static void imprimirRelatorio(GerenciadorMemoria m) {
        System.out.println("\n==================== RELATÓRIO FINAL ====================");
        System.out.println("Algoritmo utilizado .................. " + m.getAlgoritmo());
        System.out.println("Tamanho total da memória ............. " + m.getTamanhoTotal() + " bytes");
        System.out.println("Memória ocupada ...................... " + m.totalOcupado() + " bytes");
        System.out.println("Memória livre ........................ " + m.totalLivre() + " bytes");
        System.out.printf( "Utilização da memória ................ %.2f%%%n", m.utilizacaoPercentual());
        System.out.println("Maior brecha contígua livre .......... " + m.maiorBrecha() + " bytes");
        System.out.println("Alocações bem-sucedidas .............. " + m.getAlocacoesComSucesso());
        System.out.println("Processos não alocados (total) ....... " + m.getTotalFalhas());
        System.out.println("   - por falta de espaço ............. " + m.getFalhasPorEspaco());
        System.out.println("   - por fragmentação externa ........ " + m.getFalhasPorFragmentacao());
        System.out.println("Estado final da memória:");
        m.imprimirEstado();
        System.out.println("========================================================");
    }
}
