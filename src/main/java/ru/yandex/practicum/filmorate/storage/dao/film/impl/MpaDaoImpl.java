package ru.yandex.practicum.filmorate.storage.dao.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.film.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("MpaDaoImpl")
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getMpa(Integer id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
        } else {
            throw new MpaNotFoundException(String.format("Возрастной рейтинг с id %d не найден.", id));
        }
    }

    @Override
    public List<MPA> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private MPA mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(
                rs.getInt("mpa_id"),
                rs.getString("name")
        );
    }
}
