package ru.spbau.gorokhov.junitlite.testclasses;

import ru.spbau.gorokhov.junitlite.annotations.Test;

public class TestClass2 extends TestClass {
    @Test(expected = NullPointerException.class)
    public void test1() throws NullPointerException {
        counter++;
        throw new NullPointerException();
    }
}