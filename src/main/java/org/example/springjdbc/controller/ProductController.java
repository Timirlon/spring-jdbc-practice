package org.example.springjdbc.controller;

import lombok.RequiredArgsConstructor;
import org.example.springjdbc.dao.ProductDao;
import org.example.springjdbc.model.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductDao productDao;

    @GetMapping
    public List<Product> getAll() {
        return productDao.getAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable int id) {
        return productDao.getById(id);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productDao.create(product);
    }

    @PutMapping
    public Product update(@RequestBody Product product) {
        return productDao.update(product);
    }
}
