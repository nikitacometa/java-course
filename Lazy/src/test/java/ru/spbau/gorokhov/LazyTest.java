package ru.spbau.gorokhov;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class LazyTest<T> {
    private final static int ATTEMPTS_TO_GET = 8;

    private final CountSupplier countSupplier;

    public LazyTest(Supplier<T> supplier) {
        countSupplier = new CountSupplier(supplier);
    }

    @Parameterized.Parameters
    public static Collection<Supplier<?>> getSuppliers() {
        Supplier<String> helloWorldSupplier = () -> "Hello, world!";
        Supplier<Object> nullSupplier = () -> null;
        Supplier<Integer> mmxviiSupplier = () -> 2017;

        int numbersCount = 10_000_000, maxNumber = 1_000_000;
        Random random = new Random(System.nanoTime());
        Supplier<Long> randomNumbersSumSupplier = () -> {
            long sum = 0;
            for (int i = 0; i < numbersCount; i++) {
                sum += random.nextInt(maxNumber) + 1;
            }
            return sum;
        };

        return Stream.of(
                helloWorldSupplier,
                nullSupplier,
                mmxviiSupplier,
                randomNumbersSumSupplier
        ).collect(Collectors.toList());
    }

    @Before
    public void initCountSupplier() {
        countSupplier.resetCounter();
    }

    @Test
    public void singleThreadLazyTest() {
        Lazy<T> lazy = LazyFactory.createSingleThreadLazy(countSupplier);
        T result = lazy.get();

        for (int i = 0; i < ATTEMPTS_TO_GET; i++) {
            T newResult = lazy.get();
            assertSame(result, newResult);
        }

        assertThat(countSupplier.getGetCount(), is(1));
    }

    @Test
    public void multiThreadLazyTest() throws InterruptedException {
        Lazy<T> lazy = LazyFactory.createMultiThreadLazy(countSupplier);

        checkResultUniqueness(lazy);

        assertThat(countSupplier.getGetCount(), is(1));
    }

    @Test
    public void multiThreadLockFreeLazyTest() throws InterruptedException {
        Lazy<T> lazy = LazyFactory.createMultiThreadLockFreeLazy(countSupplier);

        checkResultUniqueness(lazy);
    }

    private void checkResultUniqueness(Lazy<T> lazy) throws InterruptedException {
        List<T> results = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < ATTEMPTS_TO_GET; i++) {
            Thread newThread = new Thread(() -> {
                T newResult = lazy.get();
                synchronized (results) {
                    results.add(newResult);
                }
            });
            newThread.start();
            threads.add(newThread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        T firstResult = null;
        for (T result : results) {
            if (firstResult == null) {
                firstResult = result;
            } else {
                assertSame(firstResult, result);
            }
        }
    }

    @RequiredArgsConstructor
    private class CountSupplier implements Supplier<T> {
        private final Supplier<T> supplier;
        @Getter
        private int getCount = 0;

        @Override
        public T get() {
            getCount++;
            return supplier.get();
        }

        public void resetCounter() {
            getCount = 0;
        }
    }
}
