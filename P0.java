import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class P0 {

    // Guarda os resultados dos testes
    static class Resultado {
        String tamanho;
        int totalIds;
        int threads;
        long tempoMs;
        String status;

        public Resultado(String tamanho, int totalIds, int threads, long tempoMs, String status) {
            this.tamanho = tamanho;
            this.totalIds = totalIds;
            this.threads = threads;
            this.tempoMs = tempoMs;
            this.status = status;
        }
    }

    public static void main(String[] args) {
        List<Resultado> tabelaResultados = new ArrayList<>();
        
        // Defincao do N > 1 escolhido para os testes
        int N = 4;

        System.out.println("         INICIANDO TESTES         ");

        rodarCenario("Pequena", 50, 1, tabelaResultados);
        rodarCenario("Pequena", 50, N, tabelaResultados);
        rodarCenario("Media", 200, 1, tabelaResultados);
        rodarCenario("Media", 200, N, tabelaResultados);
        rodarCenario("Grande", 1000, 1, tabelaResultados);
        rodarCenario("Grande", 1000, N, tabelaResultados);

        // Imprimir a tabela com o resultado no console
        imprimirRelatorio(tabelaResultados);
    }

    // Funcao auxiliar para rodar um teste completo
    private static void rodarCenario(String nomeTamanho, int qtdIds, int numThreads, List<Resultado> listaResultados) {
        String arqEntrada = "lista_ids_" + nomeTamanho.toLowerCase() + ".txt";
        String arqLog = "log_" + nomeTamanho.toLowerCase() + "_t" + numThreads + ".txt";

        gerarArquivoIds(arqEntrada, qtdIds);

        // Cronometra o tempo de execucao do P1
        long inicio = System.currentTimeMillis();
        String status = "OK";
        int exitCode = -1;

        try {
            //criando o processo filho P1
            ProcessBuilder pb = new ProcessBuilder("java", "P1", arqEntrada, arqLog, String.valueOf(numThreads));
            Process p1 = pb.start();

            // Aguarda P1 finalizar
            exitCode = p1.waitFor();
            long tempoFim = System.currentTimeMillis() - inicio;

            // Auditoria de termino
            if (exitCode != 0) {
                status = "ERRO (Exit Code: " + exitCode + ")";
            } else {
                // Confere o numero de linhas do log gerado
                int linhasLog = contarLinhas(arqLog);
                if (linhasLog != qtdIds) {
                    status = "enriquecimento incompleto";
                }
            }

            listaResultados.add(new Resultado(nomeTamanho, qtdIds, numThreads, tempoFim, status));

        } catch (Exception e) {
            long tempoFim = System.currentTimeMillis() - inicio;
            listaResultados.add(new Resultado(nomeTamanho, qtdIds, numThreads, tempoFim, "FALHA NA EXECUCAO"));
        }
    }

    // Script gerador simples de arquivo de IDs
    private static void gerarArquivoIds(String nomeArquivo, int quantidade) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(nomeArquivo));
            for (int i = 1; i <= quantidade; i++) {
                bw.write(String.valueOf(100 + i));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Funcao simples para contar as linhas do log
    private static int contarLinhas(String nomeArquivo) {
        int linhas = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(nomeArquivo));
            while (br.readLine() != null) {
                linhas++;
            }
            br.close();
        } catch (IOException e) {
            return -1;
        }
        return linhas;
    }

    // Imprime a tabela formatada
    private static void imprimirRelatorio(List<Resultado> lista) {
        System.out.println("\n");
        System.out.println("                        RELATORIO FINAL DE EXECUCAO                        ");
        System.out.println("");
        System.out.printf("%-10s | %-10s | %-10s | %-12s | %-25s%n", "Tamanho", "Qtd IDs", "Threads", "Tempo (ms)", "Status Auditoria");
        System.out.println("");
        
        for (Resultado r : lista) {
            System.out.printf("%-10s | %-10d | %-10d | %-12d | %-25s%n",
                    r.tamanho, r.totalIds, r.threads, r.tempoMs, r.status);
        }
        System.out.println("\n");
    }
}

