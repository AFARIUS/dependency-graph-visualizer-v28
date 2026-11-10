public record Dependency(String groupId, String artifactId) {

    // Удобный метод для вывода
    public String getFullName() {
        return groupId + ":" + artifactId;
    }

    @Override
    public String toString() {
        return getFullName();
    }

}