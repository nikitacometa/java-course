package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */
public abstract class Function1<A, V> {
    public abstract V apply(A x);

    public <T> Function1<A, T> compose(Function1<V, T> g) {
        return new Function1<A, T>() {
            public T apply(A x) {
                return g.apply(Function1.this.apply(x));
            }
        };
    }
}
