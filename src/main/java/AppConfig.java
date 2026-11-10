public record AppConfig(String packageName, String repoUrl, boolean isTestMode, int maxDepth) {

    @Override
    public String toString() {
        return "Имя пакета: " + packageName + "\n" +
                "Репозиторий: " + repoUrl + "\n" +
                "Режим тестирования: " + isTestMode + "\n" +
                "Максимальная глубина: " + maxDepth;
    }
}