package ru.yandex.practicum.filmorate.storage.dao.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("FilmDaoImpl")
@RequiredArgsConstructor
@Slf4j
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean checkFilmExist(Integer id) {
        String sqlQuery = "SELECT id FROM films WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!rowSet.next()) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден.", id));
        }
        return true;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, duration, releaseDate, mpa_id) VALUES (?,?,?,?,?)";
        Integer mpaID = film.getMpa().getId();
        KeyHolder id = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, mpaID);
            return ps;
        }, id);

        film.setId(Integer.parseInt(Objects.requireNonNull(id.getKey()).toString()));
        log.info("Новому фильму присвоен id {}", film.getId());
        String mpaName = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
        MPA mpa = jdbcTemplate.queryForObject(mpaName, this::mapRowToMpa, mpaID);
        film.setMpa(mpa);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Integer mpaId = film.getMpa().getId();
        String sqlQuery = "UPDATE films SET " +
                "name = ?," +
                "description = ?," +
                "duration = ?," +
                "releaseDate = ?," +
                "mpa_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                mpaId, film.getId());

        String mpaName = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
        MPA mpa = jdbcTemplate.queryForObject(mpaName, this::mapRowToMpa, mpaId);
        assert mpa != null;
        film.setMpa(mpa);
        return film;
    }

    @Override
    public Boolean deleteFilm(Film film) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, film.getId());
        return true;
    }

    @Override
    public Film getFilmByID(Integer id) throws FilmNotFoundException {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public Set<Integer> getFilmLikes(Integer id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        assert film != null;
        return film.getLikes();
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_likes AS lk ON f.id = lk.film_id " +
                "GROUP BY f.id ORDER BY COUNT(lk.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }


    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int mpaID = rs.getInt("mpa_id");
        String mpaName = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
        MPA mpa = jdbcTemplate.queryForObject(mpaName, this::mapRowToMpa, mpaID);

        int filmID = rs.getInt("id");
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id IN" +
                "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
        List<Genre> genres = new ArrayList<>(jdbcTemplate.query(sql, this::mapRowToGenre, filmID));

        assert mpa != null;
        return Film.builder()
                .id(filmID)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    private MPA mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(rs.getInt("mpa_id"), rs.getString("name"));
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
