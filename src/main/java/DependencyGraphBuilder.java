import java.util.*;

public class DependencyGraphBuilder {
    private final String repoUrl;
    private final int maxDepth;
    private final boolean isTestMode;
    private final Map<String, List<String>> testGraph; // Используется только в тестовом режиме
    private final Set<String> visitedNodes; // Множество обработки циклов и дубликатов (используется свойство уникальности элементов множества)

    public DependencyGraphBuilder(String repoUrl, int maxDepth, boolean isTestMode, Map<String, List<String>> testGraph) {
        this.repoUrl = repoUrl;
        this.maxDepth = maxDepth;
        this.isTestMode = isTestMode;
        this.testGraph = testGraph;
        this.visitedNodes = new HashSet<>();
    }

    public void build(String rootPackage) {
        visitedNodes.add(rootPackage);
        System.out.println("\nГраф зависимостей для " + rootPackage + " (Глубина: " + maxDepth + ")");

        // Запускаем рекурсивный BFS с 0-го уровня
        List<String> currentLevel = new ArrayList<>();
        currentLevel.add(rootPackage);

        bfsRecursive(currentLevel, 0);
    }

    /**
     * Рекурсивный BFS. На каждом шаге обрабатывает текущий уровень и вызывает себя для следующего.
     */
    private void bfsRecursive(List<String> currentLevelNodes, int currentDepth) {
        if (currentDepth >= maxDepth) {
            return;
        }

        List<String> nextLevelNodes = new ArrayList<>();

        for (String node : currentLevelNodes) {
            List<String> dependencies = getDependencies(node);

            if (dependencies.isEmpty()) continue;

            System.out.println("  ".repeat(currentDepth) + "[" + node + "]");

            for (String dep : dependencies) {
                System.out.println("  ".repeat(currentDepth + 1) + "-> " + dep);

                if (!visitedNodes.contains(dep)) {
                    visitedNodes.add(dep);
                    nextLevelNodes.add(dep);
                }
            }
        }

        if (!nextLevelNodes.isEmpty()) {
            bfsRecursive(nextLevelNodes, currentDepth + 1);
        }
    }

    /**
     * Получает зависимости узла. Либо из тестового файла, либо из интернета.
     */
    private List<String> getDependencies(String node) {
        if (isTestMode) {
            return testGraph.getOrDefault(node, Collections.emptyList());
        } else {
            try {
                List<Dependency> deps = MavenDependencyFetcher.fetchDirectDependencies(node, repoUrl);
                List<String> depNames = new ArrayList<>();
                for (Dependency d : deps) {
                    if (d.version() == null) {
                        System.out.println("  [Пропуск] " + d.getFullName() + " (версия не указана в POM)");
                    } else {
                        depNames.add(d.getFullName());
                    }
                }
                return depNames;
            } catch (Exception e) {
                System.err.println("Не удалось получить зависимости для " + node + ": " + e.getMessage());
                return Collections.emptyList();
            }
        }
    }
}