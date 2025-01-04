package org.example.springjdbc.dao.impl;

import lombok.RequiredArgsConstructor;
import org.example.springjdbc.dao.CategoryDao;
import org.example.springjdbc.dao.OptionDao;
import org.example.springjdbc.model.Category;
import org.example.springjdbc.model.Option;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OptionDaoImpl implements OptionDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String GET_ALL_QUERY = """
            SELECT o.id AS option_id, o.name AS option_name
            FROM options o
            """;

    @Override
    public List<Option> findAll() {
        return jdbcTemplate.query(GET_ALL_QUERY, this::mapRow);
    }

    @Override
    public Option findById(int id) {
        String query = GET_ALL_QUERY + " WHERE o.id = ?";

        return jdbcTemplate.query(query, this::mapRow, id)
                .stream()
                .findFirst()
                .orElseThrow();
    }

    @Override
    public Option create(Option option) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("options")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = Map.of(
                "name", option.getName(),
                "category_id", option.getCategory().getId()
        );

        int id = insert.executeAndReturnKey(map).intValue();

        option.setId(id);

        return option;
    }

    private Option mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("option_id");
        String name = rs.getString("option_name");


        return new Option(id, name, null);
    }
}
