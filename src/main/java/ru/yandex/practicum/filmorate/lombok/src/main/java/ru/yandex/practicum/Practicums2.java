package ru.yandex.practicum;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
class Point {
    private int x;
    private int y;
}

class Practicums2 {
    public static void main(String[] args) {
        // Используем статический метод of для создания объекта
        Point point = Point.of(23, -12);
        System.out.println(point);
    }
}