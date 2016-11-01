package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */

/**
 * Class representing predicate of one argument p: A -> boolean
 * @param <A> argument type
 */
public interface Predicate<A> extends Function1<A, Boolean> {
    /**
     * Applies predicate to an argument
     * @param x argument
     * @return result
     */
    Boolean apply(A x);

    /**
     * Gets predicate h which is negation to p
     * @return h(x) = !p(x)
     */
    default Predicate<A> not() {
        return x -> !apply(x);
    }

    /**
     * Gets predicate which is logical disjunction of p and some other predicate
     * @param g second predicate
     * @return h(x) = p(x) || g(x)
     */
    default Predicate<A> or(Predicate<? super A> g) {
        return x -> apply(x) || g.apply(x);
    }

    /**
     * Gets predicate which is logical conjunction of p and some other predicate
     * @param g second predicate
     * @return h(x) = p(x) && g(x)
     */
    default Predicate<A> and(Predicate<? super A> g) {
        return x -> apply(x) && g.apply(x);
    }

    /**
     * Predicate which always returns <tt>true</tt>
     */
    static <T> Predicate<T> ALWAYS_TRUE() {
        return x -> true;
    }

    /**
     * Predicate which always returns <tt>false</tt>
     */
    static <T> Predicate<T> ALWAYS_FALSE() {
        return x -> false;
    }
}
