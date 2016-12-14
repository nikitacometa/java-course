package ru.spbau.gorokhov;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class ThreadRule implements TestRule {

    private final List<Thread> threads = new ArrayList<>();
    private boolean exception = false;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();

                assertFalse(exception);
                assertFalse(threads.stream().anyMatch(Thread::isAlive));
            }
        };
    }

    public void register(Thread thread) {
        thread.setUncaughtExceptionHandler((t, e) -> exception = true);
        threads.add(thread);
    }
}