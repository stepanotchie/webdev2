package com.stephanie.webdev2;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong();

    public ProductRepository() {
        products.add(new Product(idCounter.incrementAndGet(), "Laptop", 45000, new Category("Electronics")));
        products.add(new Product(idCounter.incrementAndGet(), "Mouse", 800, new Category("Accessories")));
        products.add(new Product(idCounter.incrementAndGet(), "Keyboard", 1500, new Category("Accessories")));
        products.add(new Product(idCounter.incrementAndGet(), "Monitor", 12000, new Category("Electronics")));
        products.add(new Product(idCounter.incrementAndGet(), "Headset", 2500, new Category("Accessories")));
        products.add(new Product(idCounter.incrementAndGet(), "Printer", 8500, new Category("Electronics")));
    }

    public List<Product> findAll() {
        return products;
    }

    public Optional<Product> findById(Long id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Product save(Product product) {
        product.setId(idCounter.incrementAndGet());
        products.add(product);
        return product;
    }
}