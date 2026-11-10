import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AppTest {

    @Test
    public void testCliParserThrowsExceptionOnInvalidDepth() {
        String[] args = {"-p", "test", "-r", "url", "-d", "abc"};
        assertThrows(IllegalArgumentException.class, () -> CliParser.parse(args), "Парсер должен выдать ошибку, если глубина не число");
    }

    @Test
    public void testCliParserThrowsExceptionOnMissingArg() {
        String[] args = {"-p", "test", "-d", "3"};
        assertThrows(IllegalArgumentException.class, () -> CliParser.parse(args), "Парсер должен выдать ошибку, если нет URL репозитория");
    }

    @Test
    public void testGraphBuilderHandlesCycles() {
        Map<String, List<String>> mockGraph = new HashMap<>();
        mockGraph.put("A", Collections.singletonList("B"));
        mockGraph.put("B", Collections.singletonList("C"));
        mockGraph.put("C", Collections.singletonList("A"));

        DependencyGraphBuilder builder = new DependencyGraphBuilder("", 5, true, mockGraph);

        assertDoesNotThrow(() -> builder.build("A"), "Алгоритм не должен падать при наличии циклов");
    }
}