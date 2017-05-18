package ru.spbau.gorokhov.junitlite;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.gorokhov.junitlite.annotations.*;
import ru.spbau.gorokhov.junitlite.exceptions.InvalidTestClassException;
import ru.spbau.gorokhov.junitlite.utils.Timer;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class implements a runner to run tests in classes written using JUnitLite annotations.
 * It writes run results into specified log stream.
 */
@RequiredArgsConstructor
public class TestRunner {
    private static final String LOG_SEPARATOR = "===================================================================================";

    @NotNull
    private final PrintStream log;

    private final Timer testingTime = new Timer();

    /**
     * Runs JUnitLite tests from a specified class.
     * @param cls testing class
     * @return <tt>true</tt> if all the tests from class passed, <tt>false</tt> otherwise
     * @throws InvalidTestClassException if class is using JUnitLite annotations incorrectly
     */
    public boolean runTests(@NotNull Class<?> cls) throws InvalidTestClassException {
        log.format("Testing class %s...\n\n", cls);

        testingTime.start();

        Object classInstance;

        try {
            classInstance = getInstance(cls);
        } catch (IllegalAccessException | InstantiationException e) {
            throw errorWithLog(e, "Failed to create new instance of class %s.");
        }

        List<Method> beforeClassMethods = getAnnotatedMethods(cls, BeforeClass.class);
        List<Method> afterClassMethods = getAnnotatedMethods(cls, AfterClass.class);
        checkStaticMethods(beforeClassMethods);
        checkStaticMethods(afterClassMethods);

        List<Method> beforeMethods = getAnnotatedMethods(cls, Before.class);
        List<Method> testMethods = getAnnotatedMethods(cls, Test.class);
        List<Method> afterMethods = getAnnotatedMethods(cls, After.class);
        checkNonStaticMethods(beforeMethods);
        checkNonStaticMethods(testMethods);
        checkNonStaticMethods(afterMethods);

        executeMethods(beforeClassMethods, classInstance);

        boolean allTestsSuccessful = executeTests(beforeMethods, testMethods, afterMethods, classInstance);

        executeMethods(afterClassMethods, classInstance);

        logFinish("successfully");

        return allTestsSuccessful;
    }

    private boolean executeTests(@NotNull List<Method> beforeMethods, @NotNull List<Method> testMethods,
                              @NotNull List<Method> afterMethods, @NotNull Object classInstance) {

        log.println("Running tests...\n");

        int ignoredTests = 0, successfulTests = 0, executedTests = 0;

        Timer testTimer = new Timer();
        Timer totalTime = new Timer();
        totalTime.start();

        for (Method testMethod : testMethods) {
            Test annotation = testMethod.getAnnotation(Test.class);

            log.println(LOG_SEPARATOR);
            log.format("%s:\n\n", testMethod.getName());

            if (!annotation.ignore().equals(Test.DEFAULT_IGNORE)) {
                log.format("Test %s IGNORED because of: %s\n", testMethod.getName(), annotation.ignore());

                ignoredTests++;
            } else {
                executedTests++;
                executeMethods(beforeMethods, classInstance);

                Class<? extends Throwable> caught = Test.DEFAULT_EXPECTED;
                testTimer.start();

                try {
                    testMethod.invoke(classInstance);
                } catch (IllegalAccessException ignored) {
                    // TODO wut
                } catch (InvocationTargetException e) {
                    e.getCause().printStackTrace(log);
                    caught = e.getCause().getClass();
                }

                if (caught == annotation.expected()) {
                    if (caught != Test.DEFAULT_EXPECTED) {
                        log.println();
                    }
                    log.format("Test %s PASSED in %dms.\n", testMethod.getName(), testTimer.getTime());

                    successfulTests++;
                } else {
                    log.format("\nTest %s FAILED in %dms. ", testMethod.getName(), testTimer.getTime());

                    if (annotation.expected().equals(Test.DEFAULT_EXPECTED)) {
                        log.println("Exception was thrown.");
                    } else {
                        log.format("%s was expected, but %s was thrown.\n", annotation.expected(),
                                caught.equals(Test.DEFAULT_EXPECTED) ? "no exception" : caught);
                    }
                }

                executeMethods(afterMethods, classInstance);
            }

            log.println(LOG_SEPARATOR);

            log.format("\nTotal %d executed tests, %d IGNORED. %d/%d tests PASSED, %d/%d FAILED. Total time: %dms.\n\n",
                    executedTests, ignoredTests, successfulTests, executedTests, executedTests - successfulTests, executedTests, totalTime.getTime());
        }

        return executedTests == successfulTests;
    }

    private void executeMethods(@NotNull List<Method> methods, @NotNull Object classInstance) {
        for (Method method : methods) {
            try {
                method.invoke(classInstance);
            } catch (IllegalAccessException ignored) {
                // TODO
            } catch (InvocationTargetException e) {
                e.getCause().printStackTrace(log);
            }
        }
    }

    private void checkMethodsArePublic(@NotNull List<Method> methods) throws InvalidTestClassException {
        Optional<Method> nonPublicMethod = methods.stream()
                .filter(method -> !Modifier.isPublic(method.getModifiers())).findAny();

        if (nonPublicMethod.isPresent()) {
            throw errorWithLog("Method " + nonPublicMethod.get().getName() + " is non-public, but has to be public.");
        }
    }

    private void checkMethodsHaveNoParameters(@NotNull List<Method> methods) throws InvalidTestClassException {
        Optional<Method> methodWithParameters = methods.stream()
                .filter(method -> method.getParameterCount() > 0).findAny();

        if (methodWithParameters.isPresent()) {
            throw errorWithLog("Method " + methodWithParameters.get().getName() + " has parameters, but mustn't have it.");
        }
    }

    private void checkStaticMethods(@NotNull List<Method> methods) throws InvalidTestClassException {
        Optional<Method> nonStaticMethod = methods.stream()
                .filter(method -> !Modifier.isStatic(method.getModifiers())).findAny();

        if (nonStaticMethod.isPresent()) {
            throw errorWithLog("Method " + nonStaticMethod.get().getName() + " is non-static, but has to be static.");
        }

        checkMethodsHaveNoParameters(methods);
        checkMethodsArePublic(methods);
    }

    private void checkNonStaticMethods(@NotNull List<Method> methods) throws InvalidTestClassException {
        Optional<Method> staticMethod = methods.stream()
                .filter(method -> Modifier.isStatic(method.getModifiers())).findAny();

        if (staticMethod.isPresent()) {
            throw errorWithLog("Method " + staticMethod.get().getName() + " is static, but has to be non-static.");
        }

        checkMethodsHaveNoParameters(methods);
        checkMethodsArePublic(methods);
    }

    private void logFinish(@NotNull String testingStatus) {
        log.format("\nTesting finished %s in %dms.\n", testingStatus, testingTime.getTime());
    }

    private InvalidTestClassException errorWithLog(@NotNull String message) {
        return errorWithLog(null, message);
    }

    private InvalidTestClassException errorWithLog(@Nullable Throwable t, @NotNull String message) {
        log.format("Error! %s Testing terminated.\n\n", message);

        logFinish("with errors");

        if (t == null) {
            return new InvalidTestClassException(message);
        } else {
            t.printStackTrace(log);
            return new InvalidTestClassException(message, t);
        }
    }

    @NotNull
    private static <T extends Annotation> List<Method> getAnnotatedMethods(@NotNull Class<?> cls, @NotNull Class<T> annotation) {
        return Arrays
                .stream(cls.getMethods())
                .filter(method -> method.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }

    @NotNull
    private static <T> T getInstance(@NotNull Class<T> cls) throws IllegalAccessException, InstantiationException {
        return cls.newInstance();
    }
}
