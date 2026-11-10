import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DependencyGraphBuilderTest {

    @Test
    public void testCycleProtection() {
        Map<String, List<String>> mockGraph = new HashMap<>();
        mockGraph.put("A", Collections.singletonList("B"));
        mockGraph.put("B", Collections.singletonList("C"));
        mockGraph.put("C", Collections.singletonList("A"));

        DependencyGraphBuilder builder = new DependencyGraphBuilder("", 5, true, mockGraph);

        assertDoesNotThrow(() -> builder.build("A"));
    }

    @Test
    public void testGraphCollection() {
        Map<String, List<String>> mockGraph = new HashMap<>();
        mockGraph.put("A", Collections.singletonList("B"));
        mockGraph.put("B", Collections.emptyList());

        DependencyGraphBuilder builder = new DependencyGraphBuilder("", 5, true, mockGraph);
        builder.build("A");

        Map<String, List<String>> collected = builder.getCollectedGraph();

        assertFalse(collected.isEmpty(), "Граф не должен быть пустым");
        assertTrue(collected.containsKey("A"), "Граф должен содержать узел A");
        assertEquals("B", collected.get("A").get(0), "A должен зависеть от B");
    }

    @Test
    public void testLoadOrder() {
        Map<String, List<String>> mockGraph = new HashMap<>();
        mockGraph.put("A", Collections.singletonList("B"));
        mockGraph.put("B", Collections.singletonList("C"));
        mockGraph.put("C", Collections.emptyList());

        DependencyGraphBuilder builder = new DependencyGraphBuilder("", 5, true, mockGraph);

        assertDoesNotThrow(() -> builder.printLoadOrder("A"));
    }
}