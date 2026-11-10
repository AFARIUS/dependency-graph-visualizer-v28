public class Main {
    public static void main(String[] args) {
        try {
            AppConfig config = CliParser.parse(args);
            System.out.println(config + "\n");

            System.out.println("Прямые зависимости пакета " + config.packageName() + ":");
            var dependencies = MavenDependencyFetcher.fetchDirectDependencies(config.packageName(), config.repoUrl());

            if (dependencies.isEmpty()) {
                System.out.println("(Зависимости не найдены)");
            } else {
                for (Dependency dep : dependencies) {
                    System.out.println("- " + dep.getFullName());
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка конфигурации: " + e.getMessage());
            System.err.println("Использование: run.bat -p <package> -r <url> -d <depth> [-t]");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Ошибка получения данных: " + e.getMessage());
            System.exit(1);
        }
    }
}