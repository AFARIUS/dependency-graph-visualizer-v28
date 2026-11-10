import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CliParserTest {

    @Test
    public void testParseValidArguments() {
        String[] args = {"-p", "org.test:test:1.0", "-r", "https://repo.maven.apache.org/maven2", "-d", "3", "-t"};
        AppConfig config = CliParser.parse(args);

        assertEquals("org.test:test:1.0", config.packageName());
        assertEquals("https://repo.maven.apache.org/maven2", config.repoUrl());
        assertEquals(3, config.maxDepth());
        assertTrue(config.isTestMode());
    }

    @Test
    public void testMissingRequiredArgument() {
        String[] args = {"-p", "org.test:test:1.0", "-d", "3"}; // Нет -r
        assertThrows(IllegalArgumentException.class, () -> CliParser.parse(args));
    }

    @Test
    public void testInvalidDepthFormat() {
        String[] args = {"-p", "pkg", "-r", "url", "-d", "abc"}; // Не число
        assertThrows(IllegalArgumentException.class, () -> CliParser.parse(args));
    }

    @Test
    public void testNegativeDepth() {
        String[] args = {"-p", "pkg", "-r", "url", "-d", "-5"}; // Отрицательное число
        assertThrows(IllegalArgumentException.class, () -> CliParser.parse(args));
    }
}