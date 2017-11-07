package ru.spbau.gorokhov.junitlite.utils;

/**
 * Simple class for measuring time in milliseconds.
 */
public class Timer {
    private long startTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - startTime;
    }
}
