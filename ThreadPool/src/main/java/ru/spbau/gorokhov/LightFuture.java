package ru.spbau.gorokhov;

import java.util.function.Function;

public interface LightFuture<R> {
    boolean isReady();

    R get() throws InterruptedException, LightException;

    <T> LightFuture<T> thenApply(Function<R, T> function);

    void execute();

    void cancel();
}
