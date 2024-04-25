package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Boolean deleteUser(User user) {
        users.remove(user.getId());
        return true;
    }

    @Override
    public User getUserByID(Integer id) throws UserNotFoundException {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        Set<Integer> friendsID = users.get(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer friend : friendsID) {
            friends.add(getUserByID(friend));
        }
        return friends;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
