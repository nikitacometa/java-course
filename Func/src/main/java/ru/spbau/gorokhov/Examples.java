package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */
public class Examples {
    private Examples() {}

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

    public static Function2<String, Integer, String> removeChar = new Function2<String, Integer, String>() {
        @Override
        public String apply(String x, Integer y) {
            return x.substring(0, y) + x.substring(y + 1);
        }
    };
}
