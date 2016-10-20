package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */

/**
 * Class representing predicate of one argument p: A -> boolean
 * @param <A> argument type
 */
public abstract class Predicate<A> extends Function1<A, Boolean> {
    /**
     * Applies predicate to an argument
     * @param x argument
     * @return result
     */
    public abstract Boolean apply(A x);

    /**
     * Gets predicate h which is negation to p
     * @return h(x) = !p(x)
     */
    public Predicate<A> not() {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return !Predicate.this.apply(x);
            }
        };
    }

    /**
     * Gets predicate which is logical disjunction of p and some other predicate
     * @param g second predicate
     * @return h(x) = p(x) || g(x)
     */
    public Predicate<A> or(Predicate<A> g) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return Predicate.this.apply(x) || g.apply(x);
            }
        };
    }

    /**
     * Gets predicate which is logical conjunction of p and some other predicate
     * @param g second predicate
     * @return h(x) = p(x) && g(x)
     */
    public Predicate<A> and(Predicate<A> g) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return Predicate.this.apply(x) && g.apply(x);
            }
        };
    }

    /**
     * Predicate which always returns <tt>true</tt>
     */
    public static final Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public Boolean apply(Object x) {
            return true;
        }
    };

    /**
     * Predicate which always returns <tt>false</tt>
     */
    public static final Predicate ALWAYS_FALSE = ALWAYS_TRUE.not();
}
