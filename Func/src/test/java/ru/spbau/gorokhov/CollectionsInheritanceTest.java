package ru.spbau.gorokhov;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 16.10.2016.
 */
public class CollectionsInheritanceTest {
    private class Point {
        protected int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) {
                return false;
            }
            Point that = (Point) o;
            return this.x == that.x && this.y == that.y;
        }
    }

    private class Point3D extends Point {
        private int z;

        public Point3D(int x, int y, int z) {
            super(x, y);
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point3D)) {
                return false;
            }
            Point3D that = (Point3D) o;
            return this.x == that.x && this.y == that.y && this.z == that.z;
        }
    }

    private Function1<Point, Integer> squaredVectorLength = p -> p.x * p.x + p.y * p.y;

    private Predicate<Point> isShortVector = p -> squaredVectorLength.apply(p) <= 100;

    private Point3D a, b, c, d, e, f, g;
    private List<Point3D> points;

    @Before
    public void init() {
        a = new Point3D(1, 1, 2);
        b = new Point3D(2, 2, 8);
        c = new Point3D(10, 10, 200);
        d = new Point3D(3, 4, 25);
        e = new Point3D(6, 8, 100);
        f = new Point3D(20, 20, 800);
        g = new Point3D(4, 4, 32);
        points = Arrays.asList(a, b, c, d, e, f, g);
    }

    @Test
    public void map() throws Exception {
        List<Integer> actual = Collections.map(squaredVectorLength, points);
        List<Integer> expected = Arrays.asList(2, 8, 200, 25, 100, 800, 32);

        assertEquals(expected, actual);
    }

    @Test
    public void filter() throws Exception {
        List<Point3D> actual = Collections.filter(isShortVector, points);
        List<Point3D> expected = Arrays.asList(a, b, d, e, g);

        assertEquals(expected, actual);
    }

    @Test
    public void takeWhile() throws Exception {
        List<Point3D> actual = Collections.takeWhile(isShortVector, points);
        List<Point3D> expected = Arrays.asList(a, b);

        assertEquals(expected, actual);
    }

    @Test
    public void takeUnless() throws Exception {
        List<Point3D> actual = Collections.takeUnless(isShortVector.not(), points);
        List<Point3D> expected = Arrays.asList(a, b);

        assertEquals(expected, actual);
    }

    private Function2<Integer, Point, Integer> sumIntAndSquaredVectorLength = (x, y) -> x + squaredVectorLength.apply(y);

    @Test
    public void foldl() throws Exception {
        foldCheck(Collections.foldl(sumIntAndSquaredVectorLength, 0, points));
    }

    @Test
    public void foldr() throws Exception {
        foldCheck(Collections.foldr(sumIntAndSquaredVectorLength, 0, points));
    }

    private void foldCheck(int actual) {
        int expected = 0;
        for (Point3D p : points) {
            expected += p.x * p.x + p.y * p.y;
        }

        assertEquals(expected, actual);
    }
}