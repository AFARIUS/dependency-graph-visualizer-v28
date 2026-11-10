import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlantUmlGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    public void testGeneratePumlFile() throws IOException {
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("A", Collections.singletonList("B"));

        String testFileName = tempDir.resolve("test-graph.puml").toString();

        PlantUmlGenerator.generate(graph, testFileName);

        Path pumlPath = Path.of(testFileName);
        assertTrue(Files.exists(pumlPath), "Файл .puml должен быть создан");

        String content = Files.readString(pumlPath);
        assertTrue(content.contains("@startuml"), "Файл должен содержать начало PlantUML");
        assertTrue(content.contains("@enduml"), "Файл должен содержать конец PlantUML");

        assertTrue(content.contains("[\"A\"] --> [\"B\"]"), "Файл должен содержать связь A -> B в новом формате");
    }
}