package ru.spbau.gorokhov;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

import static ru.spbau.gorokhov.SecondPartTasks.*;

/**
 * Created by wackloner on 14.11.2016.
 */
public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        List<String> paths = Arrays.asList(
                Paths.get("Streams", "src", "test", "resources", "Tricky.txt").toAbsolutePath().toString(),
                Paths.get("Streams", "src", "test", "resources", "Portishead.txt").toAbsolutePath().toString()
        );
        String substring = "the";

        List<String> expected = new ArrayList<>();
        for (String path : paths) {
            try (Scanner scanner = new Scanner(new File(path))) {
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.contains(substring)) {
                        expected.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        assertEquals(
                expected,
                findQuotes(paths, substring)
        );

        assertEquals(
                Collections.emptyList(),
                findQuotes(Collections.emptyList(), "hello")
        );
    }

    @Test
    public void testPiDividedBy4() {
        double EPS = 1e-2;
        double expected = Math.PI / 4;
        double actual = piDividedBy4();
        assertTrue(Math.abs(expected - actual) < EPS);
    }

    @Test
    public void testFindPrinter() {
        fail();
    }

    @Test
    public void testCalculateGlobalOrder() {
        fail();
    }
}
