package ru.spbau.gorokhov.junitlite.testclasses;

import com.sun.javaws.exceptions.NativeLibViolationException;
import ru.spbau.gorokhov.junitlite.annotations.Test;

public class TestClass2 extends TestClass {
    @Test(expected = NativeLibViolationException.class)
    public void test1() throws NativeLibViolationException {
        counter++;
        throw new NativeLibViolationException();
    }
}