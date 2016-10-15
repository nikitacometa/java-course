package ru.spbau.gorokhov;

/**
 * Created by wackloner on 15.10.2016.
 */
public abstract class Predicate<A> extends Function1<A, Boolean> {
    public Predicate<A> not() {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return !Predicate.this.apply(x);
            }
        };
    }

    public Predicate<A> or(Predicate<A> p) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return Predicate.this.apply(x) || p.apply(x);
            }
        };
    }

    public Predicate<A> and(Predicate<A> p) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A x) {
                return Predicate.this.apply(x) && p.apply(x);
            }
        };
    }

    public static final Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public Boolean apply(Object x) {
            return true;
        }
    };

    public static final Predicate ALWAYS_FALSE = ALWAYS_TRUE.not();
}
