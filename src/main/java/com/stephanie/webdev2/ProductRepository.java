package com.stephanie.webdev2;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public ProductRepository() {
        products.add(new Product("Laptop", 45000));
        products.add(new Product("Mouse", 800));
        products.add(new Product("Keyboard", 1500));
        products.add(new Product("Monitor", 12000));
        products.add(new Product("Headset", 2500));
        products.add(new Product("Printer", 8500));
    }

    public List<Product> findAll() {
        return products;
    }
}