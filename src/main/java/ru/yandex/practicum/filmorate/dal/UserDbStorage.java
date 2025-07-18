package ru.yandex.practicum.filmorate.dal;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.Collection;

@Repository("userDbStorage")
@Primary
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User add(User user) {
        long id = insert(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    @Override
        public User findById(Long id) {
            return findOne("SELECT * FROM users WHERE id = ?", id)
                    .orElse(null);
        }

    @Override
    public Collection<User> findAll() {
        return findMany("SELECT * FROM users");
    }

    @Override
    public boolean containsEmail(String email) {
        return findOne("SELECT * FROM users WHERE email = ?", email).isPresent();
    }

    @Override
    public void addEmail(String email) {
    }

    @Override
    public void removeEmail(String email) {
    }
}
