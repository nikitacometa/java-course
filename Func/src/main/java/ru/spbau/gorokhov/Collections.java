package ru.spbau.gorokhov;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wackloner on 15.10.2016.
 */

/**
 * Class adding functionality
 */
public class Collections {
    /**
     * Maps elements with specified function
     * @param f function
     * @param a elements
     * @param <A> elements type
     * @param <V> mapping type
     * @return list with mapping values
     */
    public static <A, V> List<V> map(Function1<? super A, V> f, Iterable<A> a) {
        List<V> res = new ArrayList<>();
        for (A x : a) {
            res.add(f.apply(x));
        }
        return res;
    }

    /**
     * Filters elements with specified predicate
     * @param p predicate
     * @param a elements
     * @param <A> elements type
     * @return list of all x from a such that p(x) = <tt>true</tt>
     */
    public static <A> List<A> filter(Predicate<? super A> p, Iterable<A> a) {
        List<A> res = new ArrayList<>();
        for (A x : a) {
            if (p.apply(x)) {
                res.add(x);
            }
        }
        return res;
    }

    /**
     * Takes elements while they are good for a predicate
     * @param p predicate
     * @param a elements
     * @param <A> elements type
     * @return list
     */
    public static <A> List<A> takeWhile(Predicate<? super A> p, Iterable<A> a) {
        List<A> res = new ArrayList<>();
        for (A x : a) {
            if (!p.apply(x)) {
                break;
            }
            res.add(x);
        }
        return res;
    }

    /**
     * Takes elements while they are bad for a predicate
     * @param p predicate
     * @param a elements
     * @param <A> elements type
     * @return list
     */
    public static <A> List<A> takeUnless(Predicate<? super A> p, Iterable<A> a) {
        return takeWhile(p.not(), a);
    }

    /**
     * Folds elements with specified function from the left
     * @param f function
     * @param startValue start value
     * @param a elements
     * @param <A> elements type
     * @param <V> function result type
     * @return folding
     */
    public static <A, V> V foldl(Function2<V, ? super A, V> f, V startValue, Iterable<A> a) {
        V res = startValue;
        for (A x : a) {
            res = f.apply(res, x);
        }
        return res;
    }

    /**
     * Folds elements with specified function from the right
     * @param f function
     * @param startValue start value
     * @param a elements
     * @param <A> elements type
     * @param <V> function result type
     * @return folding
     */
    public static <A, V> V foldr(Function2<V, ? super A, V> f, V startValue, Iterable<A> a) {
        return foldr(f, startValue, a.iterator());
    }

    private static <A, V> V foldr(Function2<V, ? super A, V> f, V startValue, Iterator<A> it) {
        if (!it.hasNext()) {
            return startValue;
        }
        A element = it.next();
        return f.apply(foldr(f, startValue, it), element);
    }
}
