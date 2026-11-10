public record Dependency(String groupId, String artifactId, String version) {
    public String getFullName() {
        return groupId + ":" + artifactId + (version != null ? ":" + version : "");
    }

    @Override
    public String toString() {
        return getFullName();
    }
}