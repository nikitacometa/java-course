package ru.spbau.gorokhov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wackloner on 15.10.2016.
 */
public class Examples {
    public static Predicate<Integer> lessThan15 = x -> x < 15;

    public static Predicate<Integer> greaterThan7 = x -> x > 7;

    public static Predicate<Integer> isEven = x -> x % 2 == 0;

    public static Function1<Integer, Integer> squareNumber = x -> x * x;

    public static Function1<String, Integer> stringLength = x -> x.length();

    public static Function2<Integer, Integer, Integer> sum = (x, y) -> x + y;

    public static Function2<String, Integer, String> removeChar = (x, y) -> x.substring(0, y) + x.substring(y + 1);

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
