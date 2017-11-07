package ru.spbau.gorokhov;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.ws.Holder;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Implementation of Lazy interface that works correctly in multi thread
 * mode without using locks. Can perform computation several times but returns
 * the same resulting object for every get() call.
 *
 * @param <T> type of computation's result.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MultiThreadLockFreeLazy<T> implements Lazy<T> {
    private static final AtomicReferenceFieldUpdater<MultiThreadLockFreeLazy, Holder> resultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(MultiThreadLockFreeLazy.class, Holder.class, "result");

    @NonNull
    private final Supplier<T> supplier;
    private volatile Holder<T> result;

    /**
     * Method which returns result of the computation. Computation can
     * be performed several times by different threads, but result object
     * is the same for all calls.
     *
     * @return result of computation.
     */
    @Override
    public T get() {
        if (result == null) {
            Holder<T> newResult = new Holder<>(supplier.get());
            resultUpdater.compareAndSet(this, null, newResult);
        }
        return result.value;
    }
}
