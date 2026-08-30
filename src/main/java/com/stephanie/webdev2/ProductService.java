package com.stephanie.webdev2;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Task 3: Constructor Injection
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductsGreaterThanPrice(double threshold) {
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getPrice() > threshold)
                .collect(Collectors.toList());
    }
}