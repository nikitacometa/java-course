package ru.spbau.gorokhov;

/**
 * Interface for an object that can do lazy computations.
 *
 * @param <T> the type of computation's result.
 */
public interface Lazy<T> {
    /**
     * Method that returns computation's result.
     *
     * @return result of computation.
     */
    T get();
}
