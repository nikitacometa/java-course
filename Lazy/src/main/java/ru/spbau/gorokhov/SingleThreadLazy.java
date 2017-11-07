package ru.spbau.gorokhov;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.ws.Holder;
import java.util.function.Supplier;

/**
 * Implementation of Lazy interface that works correctly with single thread,
 * performing computation only for first get() call and returning the same
 * resulting object every other call.
 *
 * @param <T> type of computation's result.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SingleThreadLazy<T> implements Lazy<T> {
    @NonNull
    private final Supplier<T> supplier;
    private Holder<T> result;

    /**
     * Method which returns result of the computation, performing
     * the computation only for it's first call and returning the
     * same result object for all other calls.
     *
     * @return result of computation.
     */
    @Override
    public T get() {
        if (result == null) {
            result = new Holder<>(supplier.get());
        }
        return result.value;
    }
}
