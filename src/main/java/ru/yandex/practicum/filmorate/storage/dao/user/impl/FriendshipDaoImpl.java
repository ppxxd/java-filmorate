package ru.yandex.practicum.filmorate.storage.dao.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.FriendshipDao;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Qualifier("FriendshipDaoImpl")
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer id, Integer friendId) {
        String sqlQuery = "MERGE INTO friendship (user_id, friend_user_id) VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_user_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery = "SELECT * FROM users u " +
                "WHERE u.id IN (SELECT friend_user_id FROM friendship fs WHERE fs.user_id = ?) " +
                "AND u.id IN (SELECT friend_user_id FROM friendship fs WHERE fs.user_id = ?)";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id, otherId);
        return new HashSet<>(getUsersFromRowSet(rs));
    }

    private Set<User> getUsersFromRowSet(SqlRowSet rs) {
        Set<User> users = new HashSet<>();
        while (rs.next()) {
            users.add(
                    User.builder()
                            .id(rs.getInt("id"))
                            .email(rs.getString("email"))
                            .login(rs.getString("login"))
                            .name(rs.getString("name"))
                            .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                            .build()
            );
        }
        return users;
    }
}
