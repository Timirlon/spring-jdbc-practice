package org.example.springjdbc.dao.impl;

import lombok.RequiredArgsConstructor;
import org.example.springjdbc.dao.CategoryDao;
import org.example.springjdbc.exceptions.NotFoundException;
import org.example.springjdbc.model.Category;
import org.example.springjdbc.model.Option;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CategoryDaoImpl implements CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String GET_ALL_QUERY = """
            SELECT c.id AS category_id, c.name AS category_name,
            ARRAY_AGG(o.id) AS options_id, ARRAY_AGG(o.name) AS options_name
            FROM categories c
            JOIN options o
            ON c.id = o.category_id
            GROUP BY c.id
            ORDER BY c.id;
            """;

    @Override
    public List<Category> findAll() {
        return jdbcTemplate.query(GET_ALL_QUERY, this::mapRow);
    }

    @Override
    public Category findById(int id) {
        String sql = GET_ALL_QUERY + " WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Category create(Category category) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = Map.of("name", category.getName());
        int id = insert.executeAndReturnKey(map).intValue();

        category.setId(id);

        return category;
    }

    private Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        int categoryId = rs.getInt("category_id");
        String categoryName = rs.getString("category_name");

        Category category = new Category(categoryId, categoryName);


        List<Integer> optionsId = convertRsStringToList(rs.getString("options_id"))
                .stream()
                .map(Integer::parseInt)
                .toList();

        List<String> optionsName = convertRsStringToList(rs.getString("options_name"));

        for (int i = 0; i < optionsId.size(); i++) {
            int optionId = optionsId.get(i);
            String optionName = optionsName.get(i);

            category.addOption(new Option(optionId, optionName, null));
        }

        return category;
    }

    private List<String> convertRsStringToList(String s) {
        return List.of(s.substring(1, s.length() - 1).split(","));
    }
}
