import java.util.*;

public class DependencyGraphBuilder {
    private final String repoUrl;
    private final int maxDepth;
    private final boolean isTestMode;
    private final Map<String, List<String>> testGraph;
    private final Set<String> visitedNodes;

    public DependencyGraphBuilder(String repoUrl, int maxDepth, boolean isTestMode, Map<String, List<String>> testGraph) {
        this.repoUrl = repoUrl;
        this.maxDepth = maxDepth;
        this.isTestMode = isTestMode;
        this.testGraph = testGraph;
        this.visitedNodes = new HashSet<>();
    }

    public void build(String rootPackage) {
        visitedNodes.add(rootPackage);
        System.out.println("\n--- Граф зависимостей для " + rootPackage + " (Глубина: " + maxDepth + ") ---");

        List<String> currentLevel = new ArrayList<>();
        currentLevel.add(rootPackage);

        bfsRecursive(currentLevel, 0);
    }

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

    public void printLoadOrder(String rootPackage) {
        System.out.println("\n--- Порядок загрузки зависимостей для " + rootPackage + " ---");


        Set<String> loadOrder = new LinkedHashSet<>();
        Set<String> visiting = new HashSet<>();

        buildLoadOrder(rootPackage, 0, loadOrder, visiting);

        List<String> finalOrder = new ArrayList<>(loadOrder);
        Collections.reverse(finalOrder);

        for (int i = 0; i < finalOrder.size(); i++) {
            System.out.println((i + 1) + ". " + finalOrder.get(i));
        }
    }

    private void buildLoadOrder(String node, int depth, Set<String> loadOrder, Set<String> visiting) {
        if (depth > maxDepth || visiting.contains(node)) {
            return;
        }

        visiting.add(node);

        List<String> dependencies = getDependencies(node);
        for (String dep : dependencies) {
            buildLoadOrder(dep, depth + 1, loadOrder, visiting);
        }

        visiting.remove(node);
        loadOrder.add(node);
    }

    private List<String> getDependencies(String node) {
        if (isTestMode) {
            return testGraph.getOrDefault(node, Collections.emptyList());
        } else {
            try {
                List<Dependency> deps = MavenDependencyFetcher.fetchDirectDependencies(node, repoUrl);
                List<String> depNames = new ArrayList<>();
                for (Dependency d : deps) {
                    if (d.version() == null) {
                        System.out.println("  ".repeat(maxDepth) + "[Пропуск] " + d.getFullName() + " (версия не указана)");
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