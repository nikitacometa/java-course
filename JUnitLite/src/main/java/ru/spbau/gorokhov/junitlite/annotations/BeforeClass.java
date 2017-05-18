package ru.spbau.gorokhov.junitlite.annotations;

import java.lang.annotation.*;

/**
 * A method annotated with @BeforeClass is run once after all the tests.
 * The method must be public, static and having no parameters.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeClass {
}
