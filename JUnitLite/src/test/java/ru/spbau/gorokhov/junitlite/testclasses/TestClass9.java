package ru.spbau.gorokhov.junitlite.testclasses;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.spbau.gorokhov.junitlite.annotations.Test;

// fail on creating class instance
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestClass9 extends TestClass {
    @Test
    public static void test() {}
}
