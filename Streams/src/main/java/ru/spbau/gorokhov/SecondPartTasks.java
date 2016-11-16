package ru.spbau.gorokhov;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wackloner on 14.11.2016.
 */
public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths
                .stream()
                .flatMap(path -> {
                    try {
                        return Files.lines(Paths.get(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(s -> s.contains(sequence))
                .collect(
                        Collectors.toList()
                );
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        class Point {
            double x, y;
            Point(double x, double y) {
                this.x = x;
                this.y = y;
            }
            double squaredDist(Point p) {
                return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
            }
        }
        Random random = new Random();
        int pointsNumber = 10000000;
        double squaredR = 0.25;
        Point center = new Point(0.5, 0.5);
        return Stream.generate(() -> new Point(random.nextDouble(), random.nextDouble()))
                .limit(pointsNumber)
                .filter(p -> p.squaredDist(center) <= squaredR)
                .count() * 1. / pointsNumber;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) throws Exception {
        return compositions.entrySet()
                .stream()
                .max(
                        Comparator.comparingInt(
                                entry -> entry.getValue()
                                .stream()
                                .mapToInt(String::length)
                                .sum()
                        )
                )
                .orElseThrow(() -> new Exception("Empty map!"))
                .getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(order -> order.entrySet().stream())
                .collect(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.summingInt(Map.Entry::getValue)
                        )
                );
    }
}