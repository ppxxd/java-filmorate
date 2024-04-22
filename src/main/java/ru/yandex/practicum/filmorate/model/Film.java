package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private int id;
    @NonNull
    @NotBlank
    @NotEmpty
    private String name;
    @Size(max = 200)
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private List<Genre> genres = new ArrayList<>();
    @NonNull
    private MPA mpa;
    private final Set<Integer> likes = new HashSet<>();

    public int getLikesAmount() {
        return likes.size();
    }
}
