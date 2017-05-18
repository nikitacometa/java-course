package ru.spbau.gorokhov.junitlite.annotations;

import java.lang.annotation.*;

/**
 * A method annotated with @After is run before every test.
 * The method must be public, non-static and having no parameters.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
}
