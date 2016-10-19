package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */

/**
 * Class representing function of one argument f: A -> V
 * @param <A> argument type
 * @param <V> result type
 */
public abstract class Function1<A, V> {
    /**
     * Applies function to an argument
     * @param x argument
     * @return function result
     */
    public abstract V apply(A x);

    /**
     * Composes f with function g
     * @param g composing function
     * @param <T> result type of g
     * @return h(x) = g(f(x))
     */
    public <T> Function1<A, T> compose(Function1<V, T> g) {
        return new Function1<A, T>() {
            public T apply(A x) {
                return g.apply(Function1.this.apply(x));
            }
        };
    }
}
