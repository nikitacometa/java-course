package ru.spbau.gorokhov.junitlite.exceptions;

import ru.spbau.gorokhov.junitlite.TestRunner;

/**
 * Thrown when a class that can't be tested is given to {@link TestRunner}.
 *
 * Possible reasons:
 * <UL>
 *     <LI>Class with specified name doesn't exist.</LI>
 * </UL>
 */
public class InvalidTestClassException extends Exception {
    public InvalidTestClassException(String message) {
        super(message);
    }

    public InvalidTestClassException(String message, Throwable t) {
        super(message, t);
    }
}
