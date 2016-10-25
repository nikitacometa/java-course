package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */

/**
 * Class representing function of two arguments f: F -> S -> V
 * @param <F> first argument type
 * @param <S> second argument type
 * @param <V> result type
 */
public abstract class Function2<F, S, V> {
    /**
     * Applies f to arguments
     * @param x first argument
     * @param y second argument
     * @return result
     */
    public abstract V apply(F x, S y);

    /**
     * Composes f with function of one argument
     * @param g composing function
     * @param <T> result type of g
     * @return h(x, y) = g(f(x, y))
     */
    public <T> Function2<F, S, T> compose(Function1<? super V, T> g) {
        return new Function2<F, S, T>() {
            @Override
            public T apply(F x, S y) {
                return g.apply(Function2.this.apply(x, y));
            }
        };
    }

    /**
     * Binds first argument of f
     * @param x binding value
     * @return h(y) = f(x, y)
     */
    public Function1<S, V> bind1(F x) {
        return new Function1<S, V>() {
            @Override
            public V apply(S y) {
                return Function2.this.apply(x, y);
            }
        };
    }

    /**
     * Binds second argument of f
     * @param y binding value
     * @return h(x) = f(x, y)
     */
    public Function1<F, V> bind2(S y) {
        return new Function1<F, V>() {
            @Override
            public V apply(F x) {
                return Function2.this.apply(x, y);
            }
        };
    }

    /**
     * Curries f
     * @return h(x) = g(y) = f(x, y)
     */
    public Function1<F, Function1<S, V>> curry() {
        return new Function1<F, Function1<S, V>>() {
            @Override
            public Function1<S, V> apply(F x) {
                return Function2.this.bind1(x);
            }
        };
    }
}
