package ru.spbau.gorokhov;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.ws.Holder;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MultiThreadLockFreeLazy<T> implements Lazy<T> {
    private static final AtomicReferenceFieldUpdater<MultiThreadLockFreeLazy, Holder> resultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(MultiThreadLockFreeLazy.class, Holder.class, "result");

    @NonNull
    private final Supplier<T> supplier;
    private volatile Holder<T> result;

    @Override
    public T get() {
        if (result == null) {
            Holder<T> newResult = new Holder<>(supplier.get());
            resultUpdater.compareAndSet(this, null, newResult);
        }
        return result.value;
    }
}
