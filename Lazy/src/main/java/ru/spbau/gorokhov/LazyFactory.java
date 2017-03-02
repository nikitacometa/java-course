package ru.spbau.gorokhov;


import java.util.function.Supplier;

public class LazyFactory {
    private LazyFactory() {}

    public static <T> Lazy<T> createSingleThreadLazy(Supplier<T> supplier) {
        return new SingleThreadLazy<>(supplier);
    }

    public static <T> Lazy<T> createMultiThreadLazy(Supplier<T> supplier) {
        return new MultiThreadLazy<>(supplier);
    }

    public static <T> Lazy<T> createMultiThreadLockFreeLazy(Supplier<T> supplier) {
        return new MultiThreadLockFreeLazy<>(supplier);
    }
}
