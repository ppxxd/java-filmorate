package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

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
//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
