package org.example.springjdbc.dao;

import org.example.springjdbc.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAll();

    Product getById(int id);

    Product create(Product product);

    Product update(Product product);
}
