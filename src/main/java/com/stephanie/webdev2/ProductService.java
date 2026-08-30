package com.stephanie.webdev2;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public List<Product> getProductsGreaterThanPrice(List<Product> products, double threshold) {
        return products.stream()
                .filter(product -> product.getPrice() > threshold)
                .collect(Collectors.toList());
    }
}
