public class CliParser {
    public static AppConfig parse(String[] args) {
        String packageName = null;
        String repoUrl = null;
        String depthStr = null;
        boolean isTestMode = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                if (i + 1 < args.length) packageName = args[++i];
            } else if (args[i].equals("-r")) {
                if (i + 1 < args.length) repoUrl = args[++i];
            } else if (args[i].equals("-d")) {
                if (i + 1 < args.length) depthStr = args[++i];
            } else if (args[i].equals("-t")) {
                isTestMode = true;
            }
        }

        if (packageName == null) throw new IllegalArgumentException("Отсутствует обязательный параметр: -p (Имя пакета)");
        if (repoUrl == null) throw new IllegalArgumentException("Отсутствует обязательный параметр: -r (URL репозитория)");
        if (depthStr == null) throw new IllegalArgumentException("Отсутствует обязательный параметр: -d (Глубина)");

        int maxDepth;
        try {
            maxDepth = Integer.parseInt(depthStr);
            if (maxDepth <= 0) throw new IllegalArgumentException("Глубина должна быть положительным числом больше нуля");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Параметр -d должен быть целым числом");
        }

        return new AppConfig(packageName, repoUrl, isTestMode, maxDepth);
    }
}