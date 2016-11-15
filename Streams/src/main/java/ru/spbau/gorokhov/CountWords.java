package ru.spbau.gorokhov;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wackloner on 15.11.2016.
 */
public class CountWords {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the name of file:");
        Scanner cons = new Scanner(System.in);
        String fileName = cons.nextLine();
        Stream<String> linesStream = Files.lines(Paths.get(fileName));
        linesStream
                .flatMap(s -> Arrays.stream(s.split("[ –,.;:?!-()<>'\"\\]\\[]")))
                .filter(s -> s.length() > 0)
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.summingInt(e -> 1)
                        )
                )
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .forEach(System.out::println);
    }
}