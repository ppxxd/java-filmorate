package ru.yandex.practicum.filmorate.service.inmemory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    private static int id = 1;

    @Autowired
    public UserService(@Qualifier("inMemoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    public int generateID() {
        return id++;
    }

    public User addFriend(Integer id, Integer friendId) throws UserNotFoundException {
        storage.getUserByID(id).addFriend(friendId);
        storage.getUserByID(friendId).addFriend(id);
        return storage.getUserByID(id);
    }

    public User deleteFriend(Integer id, Integer friendId) throws UserNotFoundException {
        storage.getUserByID(id).removeFriend(friendId);
        storage.getUserByID(friendId).removeFriend(id);
        return storage.getUserByID(id);
    }

    public List<User> getMutualFriendsList(Integer user1ID, Integer user2ID) throws UserNotFoundException {
        User user1 = storage.getUserByID(user1ID);
        User user2 = storage.getUserByID(user2ID);
        List<Integer> mutualFriendsIDs = user1.getFriends().stream().filter(user2.getFriends()::contains)
                .collect(Collectors.toList());
        List<User> mutualFriendObjects = new ArrayList<>();
        mutualFriendsIDs
                .forEach(friendsId -> mutualFriendObjects.add(storage.getUserByID(friendsId)));
        return mutualFriendObjects;
    }

    public User createUser(User user) {
        if (storage.getUserByID(user.getId()) == null) {
            throw new ValidationException("Пользователь с таким id уже существует.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректные данные пользователя.");
        }
        user.setId(generateID());
        storage.addUser(user);
        return user;
    }

    public User updateUser(User user) throws UserNotFoundException {
        if (storage.getUserByID(user.getId()) == null) {
            throw new UserNotFoundException("Пользователь с таким id не существует.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректные данные пользователя.");
        }
        storage.updateUser(user);
        return user;
    }

    public Boolean deleteUser(User user) {
        return storage.deleteUser(user);
    }

    public List<User> findAll() {
        return new ArrayList<>(storage.getUsers());
    }

    public List<User> getUserFriends(Integer id) throws UserNotFoundException {
        ArrayList<User> friendList = new ArrayList<>();
        Set<Integer> friends = storage.getUserByID(id).getFriends();
        friends.forEach(friendId -> friendList.add(storage.getUserByID(friendId)));
        return friendList;
    }

    public Integer getId() {
        return id;
    }
}
