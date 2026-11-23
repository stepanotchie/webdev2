package com.catalog.dto;

import com.catalog.model.Product;
import com.catalog.model.ProductStatus;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        ProductStatus status,
        Long categoryId,
        String categoryName) {

    /**
     * Must be called inside the service transaction because category is lazily
     * loaded.
     */
    public static ProductResponseDTO from(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getCategory().getId(),
                product.getCategory().getName());
    }
}
