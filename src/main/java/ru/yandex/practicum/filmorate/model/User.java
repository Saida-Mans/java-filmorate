package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class User {

    @NotNull(message = "ID обязателен при обновлении", groups = OnUpdate.class)
    private Long id;

    @NotBlank(message = "Email обязателен при создании", groups = OnCreate.class)
    @Email(message = "Email должен быть корректным", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелов", groups = {OnCreate.class, OnUpdate.class})
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой", groups = {OnCreate.class, OnUpdate.class})
    @PastOrPresent(message = "Дата рождения не должна быть в будущем", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;
}

