package ru.spbau.gorokhov;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 16.10.2016.
 */
public class PredicateTest {
    @Test
    public void apply() throws Exception {
        assertTrue(Examples.isEven.apply(420));

        assertFalse(Examples.isEven.apply(1337));
    }

    @Test
    public void not() throws Exception {
        Predicate<Integer> isOdd = Examples.isEven.not();

        assertFalse(isOdd.apply(420));

        assertTrue(isOdd.apply(1337));
    }

    @Test
    public void or() throws Exception {
        Predicate<Integer> isEvenOrGreaterThan7 = Examples.isEven.or(Examples.greaterThan7);

        for (int i = 1; i <= 15; i++) {
            if (i % 2 == 0 || i > 7) {
                assertTrue(isEvenOrGreaterThan7.apply(i));
            } else {
                assertFalse(isEvenOrGreaterThan7.apply(i));
            }
        }
    }

    @Test
    public void and() throws Exception {
        Predicate<Integer> between7and15 = Examples.lessThan15.and(Examples.greaterThan7);

        for (int i = 1; i <= 20; i++) {
            if (7 < i && i < 15) {
                assertTrue(between7and15.apply(i));
            } else {
                assertFalse(between7and15.apply(i));
            }
        }
    }

    @Test
    public void alwaysTrue() throws Exception {
        assertTrue(Predicate.ALWAYS_TRUE.apply("AU rules"));

        assertTrue(Predicate.ALWAYS_TRUE.apply(10101010));

        assertTrue(Predicate.ALWAYS_TRUE.apply(Predicate.ALWAYS_FALSE));
    }

    @Test
    public void alwaysFalse() throws Exception {
        assertFalse(Predicate.ALWAYS_FALSE.apply("AU rules"));

        assertFalse(Predicate.ALWAYS_FALSE.apply(100101010));

        assertFalse(Predicate.ALWAYS_FALSE.apply(Predicate.ALWAYS_TRUE));
    }
}