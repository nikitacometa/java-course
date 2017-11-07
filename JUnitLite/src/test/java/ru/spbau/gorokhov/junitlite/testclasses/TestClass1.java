package ru.spbau.gorokhov.junitlite.testclasses;

import ru.spbau.gorokhov.junitlite.annotations.Test;

public class TestClass1 extends TestClass {
    @Test(ignore = "rap > rock")
    public void test1() {
        counter++;
    }
}