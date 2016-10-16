package ru.spbau.gorokhov;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wackloner on 15.10.2016.
 */
public class Collections {
    private Collections() {}

    public static <A, V> List<V> map(Function1<A, V> f, Iterable<A> a) {
        List<V> res = new ArrayList<>();
        for (A x : a) {
            res.add(f.apply(x));
        }
        return res;
    }

    public static <A> List<A> filter(Predicate<A> p, Iterable<? extends A> a) {
        List<A> res = new ArrayList<>();
        for (A x : a) {
            if (p.apply(x)) {
                res.add(x);
            }
        }
        return res;
    }

    public static <A> List<A> takeWhile(Predicate<A> p, Iterable<? extends A> a) {
        List<A> res = new ArrayList<>();
        for (A x : a) {
            if (!p.apply(x)) {
                break;
            }
            res.add(x);
        }
        return res;
    }

    public static <A> List<A> takeUnless(Predicate<A> p, Iterable<? extends A> a) {
        return takeWhile(p.not(), a);
    }

    public static <A, V> V foldl(Function2<V, A, V> f, V startValue, Iterable<? extends A> a) {
        V res = startValue;
        for (A x : a) {
            res = f.apply(res, x);
        }
        return res;
    }

    public static <A, V> V foldr(Function2<V, A, V> f, V startValue, Iterable<? extends A> a) {
        return foldr(f, startValue, a.iterator());
    }

    private static <A, V> V foldr(Function2<V, A, V> f, V startValue, Iterator<? extends A> it) {
        if (!it.hasNext()) {
            return startValue;
        }
        A element = it.next();
        return f.apply(foldr(f, startValue, it), element);
    }
}
