package ru.spbau.gorokhov.junitlite;

public class ConsoleApp {
    public static void main(String[] args) {
        Runner runner = new Runner(System.out);
        for (String className : args) {
            runner.runTests(className);
        }
    }
}
