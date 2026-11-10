public class Main {
    public static void main(String[] args) {
        try {
            AppConfig config = CliParser.parse(args);
            System.out.println(config);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка конфигурации: " + e.getMessage());
            System.err.println("Использование: run.bat -p <package> -r <url> -d <depth> [-t]");
            System.exit(1);
        }
    }
}