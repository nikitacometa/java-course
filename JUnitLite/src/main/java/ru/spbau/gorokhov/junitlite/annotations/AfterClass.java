package ru.spbau.gorokhov.junitlite.annotations;

import java.lang.annotation.*;

/**
 * A method annotated with @AfterClass is run once before all the tests.
 * The method must be public, static and having no parameters.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterClass {
}
