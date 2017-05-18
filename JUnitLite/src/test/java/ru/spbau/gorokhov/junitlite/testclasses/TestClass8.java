package ru.spbau.gorokhov.junitlite.testclasses;

import ru.spbau.gorokhov.junitlite.annotations.After;

// fail on non-static check
public class TestClass8 extends TestClass {
    @After
    public static void test() {}
}
