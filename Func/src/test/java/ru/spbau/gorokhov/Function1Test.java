package ru.spbau.gorokhov;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 16.10.2016.
 */
public class Function1Test {
    @Test
    public void apply1() throws Exception {
        int num = 123;
        int actual = Examples.squareNumber.apply(num);

        assertEquals(num * num, actual);
    }

    @Test
    public void apply2() throws Exception {
        String s = "java > c++";
        int actual = Examples.stringLength.apply(s);

        assertEquals(s.length(), actual);
    }

    @Test
    public void compose() throws Exception {
        Function1<String, Integer> squareStringLength = Examples.stringLength.compose(Examples.squareNumber);
        String s = "iOS > Android";
        int actual = squareStringLength.apply(s);

        assertEquals(s.length() * s.length(), actual);
    }
}