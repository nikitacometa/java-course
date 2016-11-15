package ru.spbau.gorokhov;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        throw new UnsupportedOperationException();
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
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
                .get() //TODO: empty map handling
                .getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(order -> order.entrySet().stream())
                .collect(
                        HashMap::new,
                        (Map<String, Integer> map, Map.Entry<String, Integer> element) -> map.put(element.getKey(), map.getOrDefault(element.getKey(), 0) + element.getValue()),
                        (map1, map2) -> map1.putAll(map2)
                );
    }
}