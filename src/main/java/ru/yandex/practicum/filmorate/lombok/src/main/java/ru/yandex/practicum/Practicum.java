package ru.yandex.practicum;

import lombok.Getter;
import lombok.Setter;


class Cat {

    @Getter
    @Setter
    private String color;

    @Getter
    @Setter
    private int age;


    @Override
    public String toString() {
        return "Cat{" +
                "color='" + color + '\'' +
                ", age=" + age +
                '}';
    }
}

public class Practicum {
    public static void main(String[] args) {
        final Cat cat = new Cat();
        cat.setColor("black");
        cat.setAge(5);
        System.out.println(cat);
    }
} 