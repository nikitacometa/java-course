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
}
