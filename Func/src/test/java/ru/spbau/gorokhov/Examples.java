package ru.spbau.gorokhov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wackloner on 15.10.2016.
 */
public class Examples {
    public static Predicate<Integer> lessThan15 = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x < 15;
        }
    };

    public static Predicate<Integer> greaterThan7 = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x > 7;
        }
    };

    public static Predicate<Integer> isEven = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer x) {
            return x % 2 == 0;
        }
    };

    public static Function1<Integer, Integer> squareNumber = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer x) {
            return x * x;
        }
    };

    public static Function1<String, Integer> stringLength = new Function1<String, Integer>() {
        @Override
        public Integer apply(String x) {
            return x.length();
        }
    };

    public static Function2<Integer, Integer, Integer> sum = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer y) {
            return x + y;
        }
    };

    public static Function2<String, Integer, String> removeChar = new Function2<String, Integer, String>() {
        @Override
        public String apply(String x, Integer y) {
            return x.substring(0, y) + x.substring(y + 1);
        }
    };

    public static Function2<Integer, Integer, List<Integer>> enumFromTo = new Function2<Integer, Integer, List<Integer>>() {
        @Override
        public List<Integer> apply(Integer x, Integer y) {
            List<Integer> res = new ArrayList<>();
            for (int i = x; i <= y; i++) {
                res.add(i);
            }
            return res;
        }
    };
}
