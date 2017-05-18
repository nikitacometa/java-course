package ru.spbau.gorokhov.junitlite;


import org.jetbrains.annotations.NotNull;
import ru.spbau.gorokhov.junitlite.exceptions.InvalidTestClassException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class implements simple console application which takes some jars,
 * loads loadable classes from there and run tests in these classes with {@link TestRunner}.
 * Testing log is written to System.out.
 */
public class ConsoleApp {
    public static void main(String[] args) {
        PrintStream log = System.out;

        log.println("TestRunner console application.\n");

        TestRunner testRunner = new TestRunner(log);

        for (String jarFileName : args) {
            List<Class<?>> testingClasses;

            log.format("\nLoading classes from %s...\n", jarFileName);

            try {
                testingClasses = loadClasses(jarFileName, log);
            } catch (IOException e) {
                log.format("Failed to load classes from %s. Error occurred:\n", jarFileName);
                e.printStackTrace(log);
                continue;
            }

            for (Class<?> testingClass : testingClasses) {
                try {
                    boolean isSuccessful = testRunner.runTests(testingClass);

                    log.format("\n%s testing finished! %s See runner log for more info.\n", testingClass,
                            isSuccessful ? "All tests passed." : "Some of the tests failed.");
                } catch (InvalidTestClassException ignored) {
                    // is already written to the log but could be handled somehow else
                }
            }
        }

        log.println("\nBye!");
    }

    @NotNull
    private static List<Class<?>> loadClasses(@NotNull String jarFileName, @NotNull PrintStream log) throws IOException {
        JarFile jarFile = new JarFile(jarFileName);
        Enumeration<JarEntry> entries = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + jarFileName +"!/") };
        URLClassLoader classLoader = URLClassLoader.newInstance(urls);

        List<Class<?>> classes = new ArrayList<>();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory() || !entry.getName().endsWith(".class")){
                continue;
            }

            // -6 because of .class
            String className = entry.getName().substring(0, entry.getName().length() - 6);
            className = className.replace('/', '.');

            Class<?> cls;
            try {
                cls = classLoader.loadClass(className);
            } catch (Throwable t) {
                log.format("\nFailed to load class %s. Error occurred:\n", className);
                t.printStackTrace(log);
                continue;
            }

            log.format("\nClass %s was loaded.\n", className);

            classes.add(cls);
        }

        return classes;
    }
}
