package org.example.springjdbc.dao.impl;

import lombok.RequiredArgsConstructor;
import org.example.springjdbc.dao.CategoryDao;
import org.example.springjdbc.dao.ProductDao;
import org.example.springjdbc.exceptions.NotFoundException;
import org.example.springjdbc.model.Category;
import org.example.springjdbc.model.Option;
import org.example.springjdbc.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {
    private final JdbcTemplate jdbcTemplate;
    CategoryDao categoryDao;

    private final static String GET_ALL_QUERY = """
 SELECT p.id AS product_id, p.name AS product_name, price, c.id AS category_id, c.name AS category_name
 FROM products p
 JOIN categories c
 ON p.category_id = c.id
 JOIN options o
 ON c.id = o.category_id
 """;

    @Override
    public List<Product> getAll() {
        return jdbcTemplate.query(GET_ALL_QUERY, this::mapRow);
    }

    @Override
    public Product getById(int id) {
        String sql = GET_ALL_QUERY + " WHERE p.id = ?";
        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Product create(Product product) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("products")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> map = Map.of(
                "name", product.getName(),
                "price", product.getPrice(),
                "category_id", product.getCategory().getId()
        );
        int id = insert.executeAndReturnKey(map).intValue();

        product.setId(id);

        return product;
    }

    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("product_id");
        String name = rs.getString("product_name");
        double price = rs.getDouble("price");

        int categoryId = rs.getInt("category_id");
        String categoryName = rs.getString("category_name");


        return new Product(id, name, price, new Category(categoryId, categoryName));
    }

    public Product update(Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?";
        jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getId());

        return product;
    }
}
