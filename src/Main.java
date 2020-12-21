import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        try (Stream<Path> walkTarefas = Files.walk(Paths.get("./entradas/"));
             Stream<Path> walkVerificar = Files.walk(Paths.get("./entradas/"))) {

            List<File> resultTarefas = walkTarefas
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().contains("tarefas"))
                    .sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getName().replace("tarefas", "").replace(".txt", ""))))
                    .collect(Collectors.toList());

            List<File> resultVerificar = walkVerificar
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(f -> f.getName().contains("verificar"))
                    .sorted(Comparator.comparingInt(a -> Integer.parseInt(a.getName().replace("verificar", "").replace(".txt", ""))))
                    .collect(Collectors.toList());

            var fileIngenua = new File("./saidaIngenua.csv");
            var fileBinaria = new File("./saidaBinaria.csv");

            fileIngenua.createNewFile();
            fileBinaria.createNewFile();

            var saidaIngenua = new PrintWriter(new FileOutputStream(fileIngenua));
            var saidaBinaria = new PrintWriter(new FileOutputStream(fileBinaria));

            new Thread(() -> {
                for (int i = 0; i < resultTarefas.size(); i++) {
                    try {
                        executarI(i, resultTarefas, resultVerificar, new BuscaIngenua(), saidaIngenua, "BuscaIngenua");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                saidaIngenua.close();
            }).start();

            new Thread(() -> {
                for (int i = 0; i < resultTarefas.size(); i++) {
                    try {
                        executarI(i, resultTarefas, resultVerificar, new BuscaBinaria(), saidaBinaria, "BuscaBinaria");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                saidaBinaria.close();
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void executarI(int i, List<File> resultTarefas, List<File> resultVerificar, Busca busca, PrintWriter saida, String tipo) throws FileNotFoundException {
        var tarefasFile = resultTarefas.get(i);
        var verificarFile = resultVerificar.get(i);

        System.out.println(String.format(
                "Executando instruções dos arquivos %s e %s com %s",
                tarefasFile.getName(), verificarFile.getName(), tipo)
        );

        var tarefasReader = new Scanner(new InputStreamReader(new FileInputStream(tarefasFile)));
        var verificarReader = new Scanner(new InputStreamReader(new FileInputStream(verificarFile)));

        List<String> instrucoes = new ArrayList<>();
        while (tarefasReader.hasNextLine()) {
            instrucoes.add(tarefasReader.nextLine());
        }

        List<String> verificar = new ArrayList<>();
        while (verificarReader.hasNextLine()) {
            verificar.add(verificarReader.nextLine());
        }

        processarInstrucoes(instrucoes, verificar, busca, saida);

        saida.flush();
    }

    static void processarInstrucoes(List<String> instrucoes, List<String> verificar, Busca busca, PrintWriter saida) {
        long tempoInicio = System.currentTimeMillis();

        for (String instrucao : instrucoes) {
            if (!"".equals(instrucao)) {
                busca.insert(instrucao);
            }
        }

        for (String instrucao : verificar) {
            busca.find(instrucao);
        }

        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;

        saida.println(tempoTotal + "," + instrucoes.size() + ",");
    }
}
