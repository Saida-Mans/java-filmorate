package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> getAllGenres() {
        return genreService.getAllGenres().stream()
                .map(GenreRowMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable Long id) {
        return GenreRowMapper.toDto(genreService.getGenreById(id));
    }
}