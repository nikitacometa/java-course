package ru.spbau.gorokhov;

import javafx.util.Pair;

/**
 * Created by wackloner on 15.10.2016.
 */
public abstract class Function2<F, S, V> {
    public abstract V apply(F x, S y);

    public <T> Function2<F, S, T> compose(Function1<V, T> g) {
        return new Function2<F, S, T>() {
            @Override
            public T apply(F x, S y) {
                return g.apply(Function2.this.apply(x, y));
            }
        };
    }

    public Function1<S, V> bind1(F x) {
        return new Function1<S, V>() {
            @Override
            public V apply(S y) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<F, V> bind2(S y) {
        return new Function1<F, V>() {
            @Override
            public V apply(F x) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<Pair<F, S>, V> curry() {
        return new Function1<Pair<F, S>, V>() {
            @Override
            public V apply(Pair<F, S> p) {
                return Function2.this.apply(p.getKey(), p.getValue());
            }
        };
    }
}
