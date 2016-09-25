package ru.spbau.gorokhov;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 025 25.09.16 in 11:44.
 */
public class HashMapTest {
    @Test
    public void put() throws Exception {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        int number = 420;
        map.put(number, number);
        assertEquals(number, (int) map.get(number));
        int prev = map.put(number, number * 2);
        assertTrue(prev == number);
    }

    @Test
    public void get() throws Exception {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        int n = 123;
        for (int i = 0; i < n; i++) {
            map.put(i, i * i * i);
        }
        for (int i = 0; i < n; i++) {
            assertTrue(map.get(i) == i * i * i);
        }
    }

    @Test
    public void contains() throws Exception {
        HashMap<Boolean, Boolean> map = new HashMap<Boolean, Boolean>();
        assertFalse(map.contains(false));
        map.put(false, true);
        assertTrue(map.contains(false));
    }

    @Test
    public void remove() throws Exception {
        HashMap<Long, Long> map = new HashMap<Long, Long>();
        long number = 123456789101112L;
        map.put(number, number);
        assertTrue(map.contains(number));
        map.remove(number);
        assertFalse(map.contains(number));
    }

    @Test
    public void clear() throws Exception {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        int n = 213;
        for (int i = 0; i < n; i++) {
            map.put(i, i * i * i + 7);
        }
        map.clear();
        for (int i = 0; i < n; i++) {
            assertFalse(map.contains(i));
        }
        assertTrue(map.size() == 0);
    }

    @Test
    public void size() throws Exception {
        HashMap<Integer, Double> map = new HashMap<Integer, Double>();
        int n = 123;
        for (int i = 0; i < n; i++) {
            map.put(i, i * Math.PI);
        }
        assertTrue(map.size() == n);
    }
}