package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    int id;
    @Email
    @NotBlank
    @NotEmpty
    String email;
    @NotNull
    @NotBlank
    @NotEmpty
    String login;
    String name;
    @PastOrPresent
    @NotNull
    LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer id) {
        friends.add(id);
    }

    public void removeFriend(Integer id) {
        friends.remove(id);
    }
}
