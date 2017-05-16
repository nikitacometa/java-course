package ru.spbau.gorokhov.junitlite;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.gorokhov.junitlite.annotations.*;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Runner {
    private final PrintStream log;

    public void runTests(@NotNull String className) {
        Class<?> cls = loadClass(className);

        if (cls == null) {
            log.format("Failed to load class %s.\n", className);
            return;
        }

        runTests(cls);
    }

    public void runTests(@NotNull Class<?> cls) {
        Object classObject = getInstance(cls);

        if (classObject == null) {
            log.format("Failed to create new instance of class %s.", cls.getCanonicalName());
            return;
        }

        List<Method> beforeClassMethods = getAnnotatedMethods(cls, BeforeClass.class);
        List<Method> afterClassMethods = getAnnotatedMethods(cls, AfterClass.class);
        List<Method> beforeMethods = getAnnotatedMethods(cls, Before.class);
        List<Method> afterMethods = getAnnotatedMethods(cls, After.class);
        List<Method> testMethods = getAnnotatedMethods(cls, Test.class);
    }

    @NotNull
    private static <T extends Annotation> List<Method> getAnnotatedMethods(Class<?> cls, Class<T> annotation) {
        return Arrays
                .stream(cls.getMethods())
                .filter(method -> method.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }

    @Nullable
    private static Class<?> loadClass(@NotNull String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    private static <T> T getInstance(@NotNull Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
