package ru.spbau.gorokhov.junitlite.testclasses;

import ru.spbau.gorokhov.junitlite.annotations.Test;

import java.io.FileNotFoundException;

public class TestClass3 extends TestClass {
    @Test(expected = FileNotFoundException.class)
    public void test1() throws NullPointerException {
        counter++;
        throw new NullPointerException();
    }
}