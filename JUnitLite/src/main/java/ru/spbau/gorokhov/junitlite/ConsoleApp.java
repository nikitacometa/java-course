package ru.spbau.gorokhov.junitlite;

import ru.spbau.gorokhov.junitlite.exceptions.InvalidTestClassException;

import java.io.PrintStream;

public class ConsoleApp {
    public static void main(String[] args) {
        PrintStream log = System.out;

        log.println("TestRunner console application.\n");

        TestRunner testRunner = new TestRunner(log);

        for (String className : args) {
            try {
                boolean isSuccessful = testRunner.runTests(className);
            } catch (InvalidTestClassException ignored) {}
        }
    }
}
