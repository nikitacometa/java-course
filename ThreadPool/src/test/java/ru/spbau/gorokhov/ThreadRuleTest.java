package ru.spbau.gorokhov;

import org.junit.Test;
import org.junit.runners.model.Statement;

public class ThreadRuleTest {

    @Test(expected = AssertionError.class)
    public void exception() throws Throwable {
        final Runnable runnable = () -> {
            throw new RuntimeException();
        };

        doTest(runnable);
    }

    @Test(expected = AssertionError.class)
    public void endless() throws Throwable {
        final Runnable runnable = () -> {
            while (true) {
            }
        };

        doTest(runnable);
    }

    @Test
    public void simple() throws Throwable {
        final Runnable runnable = () -> {
            int a = 1 + 4;
        };

        doTest(runnable);
    }

    private void doTest(Runnable runnable) throws Throwable {
        final ThreadRule rule = new ThreadRule();
        final Thread thread = new Thread(runnable);

        rule.register(thread);

        final Statement statement = rule.apply(new StartThreadStatement(thread), null);

        statement.evaluate();
    }

    private static class StartThreadStatement extends Statement {

        private final Thread thread;

        public StartThreadStatement(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void evaluate() throws Throwable {
            thread.start();
            thread.join(1000);
        }
    }
}