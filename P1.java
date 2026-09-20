import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class P1 {

    // Lista de IDs lidos do arquivo
    private static List<String> listaIds = new ArrayList<>();
    // Indice para saber qual ID sera pego pela proxima thread
    private static int indiceAtual = 0;
    
    private static BufferedWriter writerLog;

    public static void main(String[] args) {
        // Validacao basica dos parametros
        if (args.length < 3) {
            System.out.println("Uso correto: java P1 <arquivo_ids> <arquivo_log> <num_threads>");
            System.exit(1);
        }

        String arqIds = args[0];
        String arqLog = args[1];
        int numThreads = Integer.parseInt(args[2]);

        try {
            //Ler todos os IDs do arquivo para a memoria
            BufferedReader reader = new BufferedReader(new FileReader(arqIds));
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    listaIds.add(linha.trim());
                }
            }
            reader.close();

            // Abrir o arquivo de log para escrita
            writerLog = new BufferedWriter(new FileWriter(arqLog));

            // Criar e iniciar as threads
            List<Thread> threads = new ArrayList<>();
            for (int i = 1; i <= numThreads; i++) {
                String nomeThread = "Thread-" + i;
                
                // Usando a interface Runnable basica
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        processarTrabalho(nomeThread);
                    }
                });
                
                threads.add(t);
                t.start();
            }

            // Aguardar todas as threads terminarem
            for (Thread t : threads) {
                t.join();
            }

            // Fechar o arquivo de log no final
            writerLog.close();
            
            // Termina com codigo 0 (sucesso)
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    // Funcao sincronizada para pegar o proximo ID sem repeticao (Exclusao Mutua)
    private static synchronized String pegarProximoId() {
        if (indiceAtual < listaIds.size()) {
            String id = listaIds.get(indiceAtual);
            indiceAtual++;
            return id;
        }
        return null;
    }

    // Funcao para processar os IDs
    private static void processarTrabalho(String nomeThread) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while (true) {
            String id = pegarProximoId();
            if (id == null) {
                break;
            }

            // Simula a consulta na API mockada
            String respostaJson = consultarApiMock(id);
            String dataHora = sdf.format(new Date());

            // Monta a linha do log conforme a regra
            String linhaLog = dataHora + ", " + nomeThread + ", " + id + ", " + respostaJson;

            // Escrita no log com exclusao mutua (synchronized)
            escreverNoLog(linhaLog);
        }
    }

    // Funcao sincronizada para garantir exclusao mutua ao gravar no log
    private static synchronized void escreverNoLog(String linha) {
        try {
            writerLog.write(linha);
            writerLog.newLine();
            writerLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // API Mockada
    private static String consultarApiMock(String id) {
        try {
            // Simulando o tempo de resposta
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Gera um valor simulado em formato JSON valido
        double valorSimulado = 100.0 + (Double.parseDouble(id) % 500);
        return String.format(Locale.US, "{\"id\": %s, \"status\": \"ok\", \"valor\": %.2f}", id, valorSimulado);
    }
}