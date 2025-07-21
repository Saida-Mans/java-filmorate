package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.RatingDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@RestController
@RequestMapping("/mpa") // или /ratings
public class RatingController {
    private final RatingDbStorage ratingDbStorage;

    public RatingController(RatingDbStorage ratingDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
    }

    @GetMapping
    public List<Rating> getAll() {
        return ratingDbStorage.findAll();
    }

    @GetMapping("/{id}")
    public Rating getById(@PathVariable Long id) {
        return ratingDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден"));
    }
}
