package ru.yandex.practicum.filmorate.storage.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface FriendshipDao {
    void addFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    Set<User> getCommonFriends(Integer id, Integer otherId);
}
