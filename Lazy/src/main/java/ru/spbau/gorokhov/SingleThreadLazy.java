package ru.spbau.gorokhov;

import lombok.RequiredArgsConstructor;

import javax.xml.ws.Holder;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SingleThreadLazy<T> implements Lazy<T> {
    private final Supplier<T> supplier;
    private Holder<T> result;

    @Override
    public T get() {
        if (result == null) {
            result = new Holder<>(supplier.get());
        }
        return result.value;
    }
}
