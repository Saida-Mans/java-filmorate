package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    public User add(User post) {
        if (post.getName() == null || post.getName().isBlank()) {
            post.setName(post.getLogin());
        }
        post.setId(getNextId());
        users.put(post.getId(), post);
        return post;
    }

    public User update(User post) {
        User oldPost = users.get(post.getId());
        oldPost.setEmail(post.getEmail());
        oldPost.setLogin(post.getLogin());
        oldPost.setName(post.getName());
        oldPost.setBirthday(post.getBirthday());
        return oldPost;
    }

    public User findById(Long id) {
        User user = users.get(id);
        return user;
    }
    public boolean containsEmail(String email) {
        return emails.contains(email);
    }

    public void addEmail(String email) {
        emails.add(email);
    }

    public void removeEmail(String email) {
        emails.remove(email);
    }

    public Collection<User> findAll() {
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}