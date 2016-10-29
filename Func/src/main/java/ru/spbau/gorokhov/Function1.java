package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */

/**
 * Class representing function of one argument f: A -> V
 * @param <A> argument type
 * @param <V> result type
 */
public interface Function1<A, V> {
    /**
     * Applies function to an argument
     * @param x argument
     * @return function result
     */
    V apply(A x);

    /**
     * Composes f with function g
     * @param g composing function
     * @param <T> result type of g
     * @return h(x) = g(f(x))
     */
    default <T> Function1<A, T> compose(Function1<? super V, T> g) {
        return x -> g.apply(apply(x));
    }
}
