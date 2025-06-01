package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser_Success() throws ValidationException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));
        user.setName("Test User");

        User created = userController.create(user);

        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals("Test User", created.getName());
    }

    @Test
    void createUser_NameNull_SetsLoginAsName() throws ValidationException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));
        user.setName(null);

        User created = userController.create(user);

        assertEquals("testuser", created.getName());
    }

    @Test
    void createUser_InvalidEmail_ThrowsException() {
        User user = new User();
        user.setEmail("invalidemail");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));
        user.setName("Name");

        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Email не может быть пустым и должен содержать символ '@'", ex.getMessage());
    }

    @Test
    void createUser_LoginWithSpaces_ThrowsException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test user");
        user.setBirthday(LocalDate.now().minusYears(20));
        user.setName("Name");

        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    void updateUser_Success() throws ValidationException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now().minusYears(20));
        user.setName("Name");
        User created = userController.create(user);

        User update = new User();
        update.setId(created.getId());
        update.setEmail("updated@example.com");
        update.setLogin("updatedLogin");
        update.setBirthday(LocalDate.now().minusYears(20));
        update.setName("Updated Name");

        User updated = userController.update(update);

        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("updatedLogin", updated.getLogin());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void updateUser_IdNull_ThrowsException() {
        User user = new User();
        user.setId(null);

        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user));
        assertEquals("Id должен быть указан", ex.getMessage());
    }

    @Test
    void updateUser_IdNotFound_ThrowsException() {
        User user = new User();
        user.setId(999L);

        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user));
        assertEquals("Фильм с таким ID не найден", ex.getMessage());
    }
}