import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            AppConfig config = CliParser.parse(args);
            System.out.println(config + "\n");

            Map<String, java.util.List<String>> testGraph = null;

            // Если включен тестовый режим, читаем локальный файл
            if (config.isTestMode()) {
                System.out.println("Включен режим тестирования. Чтение графа из файла: " + config.repoUrl());
                testGraph = TestRepoReader.readTestGraph(config.repoUrl());
            }

            // Создаем и запускаем построение графа
            DependencyGraphBuilder builder = new DependencyGraphBuilder(config.repoUrl(), config.maxDepth(), config.isTestMode(), testGraph);

            builder.build(config.packageName());

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка конфигурации: " + e.getMessage());
            System.err.println("Использование: run.bat -p <package> -r <url/path> -d <depth> [-t]");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Ошибка выполнения: " + e.getMessage());
            System.exit(1);
        }
    }
}