package ru.spbau.gorokhov;

import javafx.util.Pair;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 16.10.2016.
 */
public class Function2Test {
    @Test
    public void apply1() throws Exception {
        int a = 234, b = 567;
        int actual = Examples.sum.apply(a, b);

        assertEquals(a + b, actual);
    }

    @Test
    public void apply2() throws Exception {
        int l = 1, r = 42;
        List<Integer> actual = Examples.enumFromTo.apply(l, r);
        List<Integer> expected = Utils.getNumbersList(l, r);

        Utils.assertListsEquals(expected, actual);
    }

    @Test
    public void compose() throws Exception {
        int a = 1337, b = 420;
        Function2<Integer, Integer, Integer> squareSum = Examples.sum.compose(Examples.squareNumber);
        int actual = squareSum.apply(a, b);

        assertEquals((a + b) * (a + b), actual);
    }

    @Test
    public void bind1() throws Exception {
        int l = 123, r = 234;
        Function1<Integer, List<Integer>> enumFromLTo = Examples.enumFromTo.bind1(l);
        List<Integer> actual = enumFromLTo.apply(r);
        List<Integer> expected = Utils.getNumbersList(l, r);

        Utils.assertListsEquals(expected, actual);
    }

    @Test
    public void bind2() throws Exception {
        int l = 1001, r = 1111;
        Function1<Integer, List<Integer>> enumFromToR = Examples.enumFromTo.bind2(r);
        List<Integer> actual = enumFromToR.apply(l);
        List<Integer> expected = Utils.getNumbersList(l, r);

        Utils.assertListsEquals(expected, actual);
    }

    @Test
    public void curry() throws Exception {
        int a = 666, b = 777;
        Function1<Integer, Function1<Integer, Integer>> curriedSum = Examples.sum.curry();
        int actual = curriedSum.apply(a).apply(b);
        int expected = Examples.sum.apply(a, b);

        assertEquals(expected, actual);
    }
}