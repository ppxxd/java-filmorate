package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.user.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceDAL {
    private final UserDao userDao;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserServiceDAL(@Qualifier("UserDaoImpl") UserDao userDao, FriendshipDao friendshipDao) {
        this.userDao = userDao;
        this.friendshipDao = friendshipDao;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        user = userDao.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (!userDao.checkUserExist(user.getId())) {
            throw new UserNotFoundException("Пользователь с таким id уже существует.");
        }
        return userDao.updateUser(user);
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (getUserFriends(userId).contains(getUser(friendId))) {
            throw new AlreadyFriendsException(String.format(
                    "Пользователь с id %d уже дружит с пользователем с id %d", friendId, userId));
        }
        friendshipDao.addFriend(userId, friendId);
        return userDao.getUserByID(userId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        if (getUserFriends(userId).contains(getUser(friendId))) {
            friendshipDao.deleteFriend(userId, friendId);
        }
        return userDao.getUserByID(userId);
    }

    public User getUser(Integer id) {
        userDao.checkUserExist(id);
        return userDao.getUserByID(id);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public List<User> getUserFriends(Integer id) {
        userDao.checkUserExist(id);
        return userDao.getUserFriends(id);
    }

    public Set<User> getMutualFriends(Integer userId, Integer friendId) {
        userDao.checkUserExist(userId);
        userDao.checkUserExist(friendId);
        return friendshipDao.getCommonFriends(userId, friendId);
    }
}
