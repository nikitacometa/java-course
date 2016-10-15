package ru.spbau.gorokhov;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wackloner on 15.10.2016.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(Examples.squareNumber.apply(5));
        System.out.println(Examples.stringLength.apply("Hello!"));

        Function1<String, Integer> squareLength = Examples.stringLength.compose(Examples.squareNumber);
        System.out.println(squareLength.apply("Hello!"));

        int n = 10;
        ArrayList<Integer> l = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            l.add(i);
        }
        List<Integer> k = Collections.map(Examples.squareNumber, l);
        for (int i : k) {
            System.out.print(i + " ");
        }
    }
}
