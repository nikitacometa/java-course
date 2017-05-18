package ru.spbau.gorokhov.junitlite.testclasses;

import ru.spbau.gorokhov.junitlite.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class TestClass4 extends TestClass {
    public static final List<String> calls = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        calls.add("BeforeClass");
    }

    @Before
    public void before() {
        calls.add("Before");
    }

    @Test
    public void test1() {
        calls.add("Test");
    }

    @Test
    public void test2() {
        calls.add("Test");
    }

    @After
    public void after() {
        calls.add("After");
    }

    @AfterClass
    public static void afterClass() {
        calls.add("AfterClass");
    }
}