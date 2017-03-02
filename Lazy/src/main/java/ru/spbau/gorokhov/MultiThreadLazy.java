package ru.spbau.gorokhov;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.xml.ws.Holder;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MultiThreadLazy<T> implements Lazy<T> {
    @NonNull
    private final Supplier<T> supplier;
    private volatile Holder<T> result;

    @Override
    public T get() {
        if (result == null) {
            synchronized (this) {
                if (result == null) {
                    result = new Holder<>(supplier.get());
                }
            }
        }
        return result.value;
    }
}
