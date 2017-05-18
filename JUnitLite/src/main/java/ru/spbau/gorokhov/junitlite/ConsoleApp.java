package ru.spbau.gorokhov.junitlite;

import org.jetbrains.annotations.NotNull;
import ru.spbau.gorokhov.junitlite.exceptions.InvalidTestClassException;

import java.io.PrintStream;

/**
 * Class implements simple console application which takes some classes names,
 * loads them from classpath and run its test with {@link TestRunner}.
 * Testing log is written to System.out.
 */
public class ConsoleApp {
    public static void main(String[] args) {
        PrintStream log = System.out;

        log.println("TestRunner console application.\n");

        TestRunner testRunner = new TestRunner(log);

        for (String className : args) {
            try {
                boolean isSuccessful = testRunner.runTests(loadClass(className));
            } catch (InvalidTestClassException ignored) {
                // is already written to the log but could be handled somehow else
            } catch (ClassNotFoundException e) {
                log.format("Class %s can't be tested because it doesn't exist in classpath.", className);
            }
        }

        log.println("Bye!");
    }

    @NotNull
    private static Class<?> loadClass(@NotNull String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
