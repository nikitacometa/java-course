package ru.spbau.gorokhov;

import com.google.common.collect.ImmutableMap;
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
        assertEquals(expected, actual, EPS);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> compositions = ImmutableMap.of(
                "Vasya", Arrays.asList("blablablablablablabla", "aaaaaaaaaaaaaaa", "bbbbbbbbbbbbb"),
                "Petya", Arrays.asList("dog", "cat", "bus", "hat", "fat"),
                "Vanya", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h")
        );

        assertEquals(
                "Vasya",
                findPrinter(compositions)
        );

        try {
            String author = findPrinter(Collections.emptyMap());
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    public void testCalculateGlobalOrder() {
        List<Map<String, Integer>> orders = Arrays.asList(
                ImmutableMap.of(
                        "Tea", 2,
                        "iPhone", 10,
                        "Pixel", 3
                ),
                ImmutableMap.of(
                        "iPhone", 100,
                        "Pixel", 30,
                        "Macbook", 7
                ),
                ImmutableMap.of(
                        "iPhone", 1,
                        "Tea", 20,
                        "Cookies", 100500
                )
        );

        Map<String, Integer> expected = ImmutableMap.of(
                "Tea", 22,
                "iPhone", 111,
                "Pixel", 33,
                "Macbook", 7,
                "Cookies", 100500
        );

        assertEquals(
                expected,
                calculateGlobalOrder(orders)
        );

        assertEquals(
                Collections.emptyMap(),
                calculateGlobalOrder(Collections.emptyList())
        );
    }
}
