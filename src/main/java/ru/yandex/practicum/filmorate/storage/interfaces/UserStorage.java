package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Set;

public interface UserStorage {

    Map<Integer, User> getStorage();

    User addUser(User user);

    User updateUser(User user);

    Boolean deleteUser(User user); //true if deleted;

    User getUserByID(Integer id) throws UserNotFoundException;

    Set<Integer> getUserFriends(Integer id);
}
