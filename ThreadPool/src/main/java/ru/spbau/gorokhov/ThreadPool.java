package ru.spbau.gorokhov;

import java.util.function.Supplier;

public interface ThreadPool {
    <R> LightFuture<R> addTask(Supplier<R> supplier);

    void shutdown();
}
