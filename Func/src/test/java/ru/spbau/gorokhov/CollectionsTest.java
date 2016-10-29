package ru.spbau.gorokhov;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 16.10.2016.
 */
public class CollectionsTest {
    @Test
    public void map() throws Exception {
        List<String> s = Arrays.asList("aa", "bdf", "sdffsd", "sdgerg", "efsdgsd", "gerger", "sf", "efsd");
        List<Integer> actual = Collections.map(Examples.stringLength, s);
        List<Integer> expected = new ArrayList<>();
        for (String t : s) {
            expected.add(t.length());
        }

        assertTrue(expected.equals(actual));
    }

    @Test
    public void filter() throws Exception {
        int l = 1, r = 20;
        List<Integer> numbers = Examples.enumFromTo.apply(l, r);
        List<Integer> actual = Collections.filter(Examples.isEven, numbers);
        List<Integer> expected = Utils.getNumbersList(2, r, 2);

        assertTrue(expected.equals(actual));
    }

    @Test
    public void takeWhile() throws Exception {
        int l = 1, r = 20;
        List<Integer> actual = Collections.takeWhile(Examples.lessThan15, Examples.enumFromTo.apply(l, r));
        List<Integer> expected = Utils.getNumbersList(l, 14);

        assertTrue(expected.equals(actual));
    }

    @Test
    public void takeUnless() throws Exception {
        int l = 1, r = 20;
        List<Integer> actual = Collections.takeUnless(Examples.greaterThan7, Examples.enumFromTo.apply(l, r));
        List<Integer> expected = Utils.getNumbersList(l, 7);

        assertTrue(expected.equals(actual));
    }

    private final static String ALPHABET = "abcdefjhijklmnopqrstuvwxyz";

    @Test
    public void foldl() throws Exception {
        List<Integer> indices = Utils.getNumbersList(0, 12);
        String actual = Collections.foldl(Examples.removeChar, ALPHABET, indices);
        String expected = "bdfhjlnprtvxz";

        assertTrue(expected.equals(actual));
    }

    @Test
    public void foldr() throws Exception {
        List<Integer> indices = Utils.getNumbersList(0, 12);
        String actual = Collections.foldr(Examples.removeChar, ALPHABET, indices);
        String expected = "nopqrstuvwxyz";

        assertTrue(expected.equals(actual));
    }
}