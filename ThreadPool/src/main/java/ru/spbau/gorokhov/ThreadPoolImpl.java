package ru.spbau.gorokhov;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {
    private final PoolThread[] threads;
    private final Queue<LightFutureImpl<?>> tasks;

    public ThreadPoolImpl(int threadsCount) {
        tasks = new LinkedList<>();
        threads = new PoolThread[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new PoolThread();
            threads[i].start();
        }
    }

    @Override
    public <R> LightFuture<R> addTask(Supplier<R> supplier) {
        LightFutureImpl<R> task = new LightFutureImpl<>(supplier);
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
        return task;
    }

    @Override
    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class LightFutureImpl<R> implements LightFuture<R> {
        private final Supplier<R> supplier;
        private R result;
        private LightException exception;

        public LightFutureImpl(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return result != null || exception != null;
        }

        @Override
        public synchronized R get() throws InterruptedException, LightException {
            while (!isReady()) {
                wait();
            }
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        @Override
        public <T> LightFuture<T> thenApply(Function<R, T> function) {
            //TODO
            return null;
        }

        public void execute() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                exception = new LightExecutionException(e);
            }
            notify();
        }

        public synchronized void cancel() {
            if (!isReady()) {
                exception = new LightOnCancelException();
            }
        }
    }

    private class PoolThread extends Thread {
        private LightFuture<?> currentTask;

        @Override
        public void run() {
            while (!isInterrupted()) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            interrupt();
                        }
                    }
                    currentTask = tasks.poll();
                    currentTask.execute();
                }
            }
        }

        @Override
        public void interrupt() {
            if (currentTask != null) {
                currentTask.cancel();
            }
            super.interrupt();
        }
    }
}
