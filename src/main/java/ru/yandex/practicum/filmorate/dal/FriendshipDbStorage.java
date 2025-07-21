package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.dal.mappers.FriendshipRowMapper;

import java.util.List;

    @Repository
    @RequiredArgsConstructor
    public class FriendshipDbStorage {

        private final JdbcTemplate jdbc;

        public void addFriend(Long userId, Long friendId) {
            String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
            jdbc.update(sql, userId, friendId, FriendshipStatus.PENDING.name());
        }

        public void removeFriend(Long userId, Long friendId) {
            String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
            jdbc.update(sql, userId, friendId);
        }

        public List<Friendship> findFriendsOfUser(Long userId) {
            String sql = "SELECT user_id, friend_id, status FROM friendships WHERE user_id = ?";
            return jdbc.query(sql, new FriendshipRowMapper(), userId);
        }
    }

