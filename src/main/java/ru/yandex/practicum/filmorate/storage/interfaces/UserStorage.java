package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    Boolean deleteUser(User user); //true if deleted;

    User getUserByID(Integer id) throws UserNotFoundException;

    List<User> getUserFriends(Integer id);

    List<User> getUsers();
}
