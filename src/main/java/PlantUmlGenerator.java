import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class PlantUmlGenerator {
    public static void generate(Map<String, List<String>> graph, String outputFileName) {
        String plantUmlCode = buildPlantUmlCode(graph);
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(plantUmlCode);
            System.out.println("PlantUML код успешно сохранен в файл: " + outputFileName);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    public static void showInBrowser(Map<String, List<String>> graph) {
        String plantUmlCode = buildPlantUmlCode(graph);
        StringBuilder hex = new StringBuilder("~h");
        for (byte b : plantUmlCode.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
            hex.append(String.format("%02x", b));
        }

        String url = "https://www.plantuml.com/plantuml/svg/" + hex;

        System.out.println("Открываем изображение графа в браузере...");
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Не удалось открыть браузер автоматически. Скопируйте ссылку вручную: \n" + url);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при открытии браузера: " + e.getMessage());
        }
    }

    private static String buildPlantUmlCode(Map<String, List<String>> graph) {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("hide stereotype\n");
        sb.append("skinparam linetype ortho\n");
        sb.append("left to right direction\n");

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String parent = entry.getKey();
            for (String child : entry.getValue()) {
                sb.append("[\"").append(parent).append("\"] --> [\"").append(child).append("\"]\n");
            }
        }

        sb.append("@enduml\n");
        return sb.toString();
    }
}