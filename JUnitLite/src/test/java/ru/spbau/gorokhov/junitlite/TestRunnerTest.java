package ru.spbau.gorokhov.junitlite;


import org.jetbrains.annotations.NotNull;
import ru.spbau.gorokhov.junitlite.exceptions.InvalidTestClassException;
import ru.spbau.gorokhov.junitlite.testclasses.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TestRunnerTest {
    private static PrintStream runnerLog;

    @org.junit.BeforeClass
    public static void initLog() throws FileNotFoundException {
        runnerLog = new PrintStream("tests_log");
    }

    @org.junit.AfterClass
    public static void closeLog() {
        runnerLog.close();
    }


    @org.junit.After
    public void resetCounter() {
        TestClass.counter = 0;
    }

    private static void assertCounter(int n) {
        assertThat(TestClass.counter, is(n));
    }


    @NotNull
    private static TestRunner getRunner() {
        return new TestRunner(runnerLog);
    }


    @org.junit.Test
    public void testIgnoring() throws InvalidTestClassException {
        getRunner().runTests(TestClass1.class);

        assertCounter(0);
    }


    @org.junit.Test
    public void testExpected() throws InvalidTestClassException {
        boolean class2 = getRunner().runTests(TestClass2.class);

        assertCounter(1); // test was run
        assertThat(class2, is(true)); // and succeed


        boolean class3 = getRunner().runTests(TestClass3.class);

        assertCounter(2); // test was run
        assertThat(class3, is(false)); // but failed
    }


    @org.junit.Test
    public void testOrder() throws InvalidTestClassException {
        getRunner().runTests(TestClass4.class);

        assertArrayEquals(
                new String[]{ "BeforeClass", "Before", "Test", "After", "Before", "Test", "After", "AfterClass"},
                TestClass4.calls.toArray()
        );
    }


    @org.junit.Test
    public void testClassValidation() {
        List<Class<? extends TestClass>> testingClasses = Arrays.asList(
                TestClass5.class, TestClass6.class, TestClass7.class, TestClass8.class, TestClass9.class
        );

        for (Class<? extends TestClass> testingClass : testingClasses) {
            try {
                getRunner().runTests(testingClass);
            } catch (InvalidTestClassException ignored) {
                // OK
                continue;
            }
            // FAIL
            fail(testingClass.getCanonicalName() + " was validated but hadn't been supposed to.");
        }
    }
}