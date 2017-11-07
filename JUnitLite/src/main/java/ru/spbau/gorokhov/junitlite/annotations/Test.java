package ru.spbau.gorokhov.junitlite.annotations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.*;

/**
 * A method annotated with @Test means to be test.
 * The method must be public, non-static and having no parameters.
 *
 * <tt>ignore</tt> that isn't default tells the runner not to run the test.
 * <tt>expected</tt> that isn't default tells the runner that
 * the test is successful only if specified exception is thrown.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    String DEFAULT_IGNORE = "";

    Class<? extends Throwable> DEFAULT_EXPECTED = NotException.class;


    String ignore() default DEFAULT_IGNORE;

    Class<? extends Throwable> expected() default NotException.class;


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class NotException extends Throwable {}
}
