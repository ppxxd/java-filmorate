package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmLikesDao;
import ru.yandex.practicum.filmorate.storage.dao.film.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.film.MpaDao;
import ru.yandex.practicum.filmorate.storage.dao.user.UserDao;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDaoImplTest {
    private final FilmDao filmStorage;
    private final UserDao userStorage;
    private final GenreDao genreStorage;
    private final MpaDao mpaStorage;
    private final FilmLikesDao likesStorage;
    private Film film;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("some film")
                .description("description some film")
                .releaseDate(LocalDate.of(2003, 8, 18))
                .duration(180)
                .genres(new ArrayList<>())
                .mpa(new MPA(1, "G"))
                .build();
    }

    @Test
    void shouldCreateFilm() throws ValidationException {
        Film newFilm = filmStorage.addFilm(film);
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldUpdateFilm() throws ValidationException {
        filmStorage.addFilm(film);
        filmStorage.updateFilm(
                Film.builder()
                        .id(1)
                        .name("some newFilm")
                        .description("new description some film")
                        .duration(150)
                        .releaseDate(LocalDate.of(2003, 7, 12))
                        .mpa(new MPA(1, "G"))
                        .genres(new ArrayList<>())
                        .build()
        );

        assertEquals("some newFilm", filmStorage.getFilmByID(1).getName());
        assertEquals("new description some film", filmStorage.getFilmByID(1).getDescription());
        Set<ConstraintViolation<Film>> violations = validator.validate(filmStorage.getFilmByID(1));
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldGetListFilmsWithoutViolations() throws ValidationException {
        filmStorage.addFilm(film);
        filmStorage.addFilm(
                Film.builder()
                        .name("some newFilm")
                        .description("new description some film")
                        .duration(150)
                        .releaseDate(LocalDate.of(2003, 7, 12))
                        .mpa(new MPA(1, "G"))
                        .genres(new ArrayList<>())
                        .build()
        );

        Optional<Set<ConstraintViolation<Film>>> violationSet = filmStorage.getFilms().stream()
                .map(film -> validator.validate(film))
                .filter(violation -> !violation.isEmpty())
                .findFirst();
        violationSet.ifPresentOrElse(
                violation -> assertTrue(violation.isEmpty()),
                () -> assertTrue(true)
        );
    }

    @Test
    void shouldGetFilmById() throws ValidationException {
        filmStorage.addFilm(film);
        assertEquals(film, filmStorage.getFilmByID(1));
    }

    @Test
    void shouldGetTopFilms() throws ValidationException {
        User user = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());

        Film film1 = filmStorage.addFilm(film);
        Film film2 = filmStorage.addFilm(
                Film.builder()
                        .name("some newFilm")
                        .description("new description some film")
                        .duration(150)
                        .releaseDate(LocalDate.of(2003, 7, 12))
                        .mpa(new MPA(1, "G"))
                        .genres(new ArrayList<>())
                        .build()
        );

        likesStorage.like(film2.getId(), user.getId());
        List<Film> topFilms = List.of(film2, film1);
        assertEquals(topFilms, filmStorage.getMostPopularFilms(2));
    }

    @Test
    void shouldThrowsInvalidCheckCheckFilmExist() throws ValidationException {
        filmStorage.addFilm(film);

        final FilmNotFoundException e = assertThrows(
                FilmNotFoundException.class,
                () -> filmStorage.checkFilmExist(2)
        );
        assertEquals("Фильм с id 2 не найден.", e.getMessage());
    }

    @Test
    void shouldGetMpaById() {
        assertEquals("R", mpaStorage.getMpa(4).getName());
    }

    @Test
    void shouldGetListAllMpa() {
        List<MPA> listMpa = List.of(
                new MPA(1, "G"),
                new MPA(2, "PG"),
                new MPA(3, "PG-13"),
                new MPA(4, "R"),
                new MPA(5, "NC-17")
        );

        assertEquals(listMpa, mpaStorage.getAllMpa());
    }

    @Test
    void shouldThrowsIfLikedTwice() throws ValidationException {
        Film film1 = filmStorage.addFilm(film);
        User user = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());
        likesStorage.like(film1.getId(), user.getId());

        assertThrows(RuntimeException.class, () -> likesStorage.like(film1.getId(), user.getId()));
    }

    @Test
    void shouldThrowsIfUnlikedTwice() throws ValidationException {
        Film film1 = filmStorage.addFilm(film);
        User user = userStorage.addUser(
                User.builder()
                        .email("andrey@gmail.com")
                        .login("andrey")
                        .name("Andrey")
                        .birthday(LocalDate.of(2003, 8, 18))
                        .build());

        Film film2 = filmStorage.addFilm(
                Film.builder()
                        .name("some newFilm")
                        .description("new description some film")
                        .duration(150)
                        .releaseDate(LocalDate.of(2003, 7, 12))
                        .mpa(new MPA(1, "G"))
                        .genres(new ArrayList<>())
                        .build()
        );

        List<Film> topFilms = List.of(film2, film1);
        likesStorage.like(film2.getId(), user.getId());
        assertEquals(topFilms, filmStorage.getMostPopularFilms(2));
        likesStorage.unlike(film2.getId(), user.getId());
        assertNotEquals(topFilms, filmStorage.getMostPopularFilms(2));
    }

    @Test
    void shouldGetGenreById() {
        assertEquals("Триллер", genreStorage.getGenre(4).getName());
    }

    @Test
    void shouldGetListGenres() {
        List<Genre> listGenres = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")
        );

        assertEquals(listGenres, genreStorage.getAllGenres());
    }

    @Test
    void shouldAddGenreInFilm() throws ValidationException {
        filmStorage.addFilm(film);
        genreStorage.addFilmGenre(film.getId(), 6);

        assertTrue(filmStorage.getFilmByID(film.getId())
                .getGenres()
                .contains(genreStorage.getGenre(6)));
    }

    @Test
    void shouldGetFilmGenres() throws ValidationException {
        filmStorage.addFilm(film);
        genreStorage.addFilmGenre(film.getId(), 6);
        genreStorage.addFilmGenre(film.getId(), 2);

        List<Genre> genres = List.of(
                genreStorage.getGenre(2),
                genreStorage.getGenre(6)
        );
        assertEquals(genres, genreStorage.getFilmGenre(film.getId()));
    }

    @Test
    void shouldDeleteGenresFromFilm() throws ValidationException {
        filmStorage.addFilm(film);
        genreStorage.addFilmGenre(film.getId(), 2);
        genreStorage.addFilmGenre(film.getId(), 6);

        assertFalse(filmStorage.getFilmByID(film.getId())
                .getGenres()
                .isEmpty());

        genreStorage.deleteFilmGenre(film.getId());

        assertTrue(filmStorage.getFilmByID(film.getId())
                .getGenres()
                .isEmpty());
    }
}