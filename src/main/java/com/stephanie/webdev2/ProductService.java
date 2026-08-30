package com.stephanie.webdev2;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopProperties shopProperties;

    public ProductService(ProductRepository productRepository, ShopProperties shopProperties) {
        this.productRepository = productRepository;
        this.shopProperties = shopProperties;
    }

    public List<Product> getProductsGreaterThanPrice(double threshold) {
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getPrice() > threshold)
                .collect(Collectors.toList());
    }

    public ShopProperties getShopProperties() {
        return shopProperties;
    }
}