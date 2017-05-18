package ru.spbau.gorokhov.junitlite.exceptions;

import ru.spbau.gorokhov.junitlite.TestRunner;

/**
 * Thrown when a class that can't be tested is given to {@link TestRunner}.
 *
 * Possible reasons:
 * <UL>
 *     <LI>Class with specified name doesn't exist.</LI>
 *     <LI>Failed to create class instance.</LI>
 *     <LI>Annotated methods are non-public, have parameters or static for
 *     {@link ru.spbau.gorokhov.junitlite.annotations.Before},
 *     {@link ru.spbau.gorokhov.junitlite.annotations.Test} and
 *     {@link ru.spbau.gorokhov.junitlite.annotations.After} or non-static for
 *     {@link ru.spbau.gorokhov.junitlite.annotations.BeforeClass} and {@link ru.spbau.gorokhov.junitlite.annotations.AfterClass}.</LI>
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
