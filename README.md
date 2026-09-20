Trabalhos de Programação Concorrente - Enriquecimento de IDs

Este projeto é um sistema em Java com o objetivo de ler uma lista de IDs, consultar uma API simulada e gravar os resultados em arquivos de log em paralelo.

O trabalho utiliza apenas mecanismos nativos do Sistema Operacional para criar processos e threads.

 Como Funciona

* **`P0.java` (Processo Pai):**
  * Usa `ProcessBuilder` para criar e rodar o processo filho `P1`.
  * Roda os testes para listas de tamanhos diferentes (Pequena, Média e Grande) e com 1 e 4 threads.
  * Cronometra o tempo e verifica se o log tem a quantidade certa de linhas (auditoria).

* **`P1.java` (Processo Filho):**
  * Cria as threads (`java.lang.Thread`) para processar os IDs ao mesmo tempo.
  * Usa exclusão mútua (`synchronized`) para garantir que nenhum ID seja repetido e para escrever no log sem corromper o arquivo.
  * Simula a consulta da API que retorna um JSON.

---

## 📂 Arquivos no Repositório

* `P0.java`: Processo pai (orquestrador).
* `P1.java`: Processo filho (trabalhador).
* `lista_ids_*.txt`: Arquivos de entrada com as listas de IDs.
* `log_*.txt`: Arquivos de log gerados nos testes.
* 
 Como Executar

 Abra o terminal na pasta do projeto e compile os arquivos:
   ```bash
   javac P1.java P0.java
