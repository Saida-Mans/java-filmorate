package ru.yandex.practicum.filmorate.dal.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.OnCreate;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Логин обязателен")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелов")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения обязательна", groups = OnCreate.class)
    @PastOrPresent(message = "Дата рождения не должна быть в будущем", groups = OnCreate.class)
    private LocalDate birthday;
}