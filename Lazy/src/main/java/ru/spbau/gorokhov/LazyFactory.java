package ru.spbau.gorokhov;

import java.util.function.Supplier;

/**
 * Factory class that can create different Lazy interface implementations instances.
 */
public class LazyFactory {
    private LazyFactory() {}

    /**
     * Method that returns single thread Lazy implementation instance
     * for performing a computation.
     *
     * @param supplier computation to be performed.
     * @param <T> computation result's type.
     * @return <t>SingleThreadLazy</t> instance.
     */
    public static <T> Lazy<T> createSingleThreadLazy(Supplier<T> supplier) {
        return new SingleThreadLazy<>(supplier);
    }

    /**
     * Method that returns multi thread Lazy implementation instance
     * for performing a computation.
     *
     * @param supplier computation to be performed.
     * @param <T> computation result's type.
     * @return <t>MultiThreadLazy</t> instance.
     */
    public static <T> Lazy<T> createMultiThreadLazy(Supplier<T> supplier) {
        return new MultiThreadLazy<>(supplier);
    }

    /**
     * Method that returns multi thread Lazy implementation without locks
     * instance for performing a computation.
     *
     * @param supplier computation to be performed.
     * @param <T> computation result's type.
     * @return <t>MultiThreadLockFreeLazy</t> instance.
     */
    public static <T> Lazy<T> createMultiThreadLockFreeLazy(Supplier<T> supplier) {
        return new MultiThreadLockFreeLazy<>(supplier);
    }
}
