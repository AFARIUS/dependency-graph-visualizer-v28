import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestRepoReader {
    /**
     * Читает файл формата "A -> B" и возвращает Map, где ключ - пакет, значение - список его зависимостей.
     */
    public static Map<String, List<String>> readTestGraph(String filePath) throws Exception {
        Map<String, List<String>> graph = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            line = line.trim();
            if (!line.contains("->")) continue;

            String[] parts = line.split("->");
            String parent = parts[0].trim();
            String child = parts[1].trim();

            graph.putIfAbsent(parent, new ArrayList<>());
            graph.get(parent).add(child);
            graph.putIfAbsent(child, new ArrayList<>());
        }
        return graph;
    }
}