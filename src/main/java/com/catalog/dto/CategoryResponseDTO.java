package com.catalog.dto;

import com.catalog.model.Category;

public record CategoryResponseDTO(Long id, String name, String description, int productCount) {

    /**
     * Must be called inside the service transaction because products is lazily
     * loaded.
     */
    public static CategoryResponseDTO from(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts().size());
    }
}
