package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email
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
}
