package ru.spbau.gorokhov;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 16.10.2016.
 */
public class Utils {
    public static List<Integer> getNumbersList(int from, int to, int step) {
        List<Integer> res = new ArrayList<>();
        for (int i = from; i <= to; i += step) {
            res.add(i);
        }
        return res;
    }

    public static List<Integer> getNumbersList(int from, int to) {
        return getNumbersList(from, to, 1);
    }

    public static <V> void assertListsEquals(List<? extends V> expected, List<? extends V> actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            assertTrue(expected.get(i).equals(actual.get(i)));
        }
    }

    public static <T> List<T> getList(T... elements) {
        List<T> res = new ArrayList<>();
        for (T element : elements) {
            res.add(element);
        }
        return res;
    }
}
