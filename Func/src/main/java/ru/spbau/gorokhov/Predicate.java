package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */
public abstract class Predicate<A> {
    public abstract boolean apply(A x);

    public Predicate<A> not() {
        return new Predicate<A>() {
            @Override
            public boolean apply(A x) {
                return !Predicate.this.apply(x);
            }
        };
    }

    public Predicate<A> or(Predicate<A> p) {
        return new Predicate<A>() {
            @Override
            public boolean apply(A x) {
                return Predicate.this.apply(x) || p.apply(x);
            }
        };
    }

    public Predicate<A> and(Predicate<A> p) {
        return new Predicate<A>() {
            @Override
            public boolean apply(A x) {
                return Predicate.this.apply(x) && p.apply(x);
            }
        };
    }

    public static final Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public boolean apply(Object x) {
            return true;
        }
    };

    public static final Predicate ALWAYS_FALSE = ALWAYS_TRUE.not();
}
