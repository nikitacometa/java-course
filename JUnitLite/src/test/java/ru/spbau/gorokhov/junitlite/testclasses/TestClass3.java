package ru.spbau.gorokhov.junitlite.testclasses;

import com.sun.javaws.exceptions.ErrorCodeResponseException;
import com.sun.javaws.exceptions.NativeLibViolationException;
import ru.spbau.gorokhov.junitlite.annotations.Test;

public class TestClass3 extends TestClass {
    @Test(expected = ErrorCodeResponseException.class)
    public void test1() throws NativeLibViolationException {
        counter++;
        throw new NativeLibViolationException();
    }
}