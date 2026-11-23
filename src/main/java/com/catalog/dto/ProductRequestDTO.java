package com.catalog.dto;

import com.catalog.model.ProductStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequestDTO(

        @NotBlank(message = "Product name is required") @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters") String name,

        @NotBlank(message = "SKU is required") @Pattern(regexp = "^[A-Z0-9]+(-[A-Z0-9]+)*$", message = "SKU must be uppercase alphanumeric, optionally hyphen-separated (e.g. PROD-101)") @Size(max = 50, message = "SKU must not exceed 50 characters") String sku,

        @NotNull(message = "Price is required") @Min(value = 1, message = "Price must be at least 1") @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimals") BigDecimal price,

        @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity must not be negative") Integer stockQuantity,

        // defaults to ACTIVE when omitted
        ProductStatus status,

        @NotNull(message = "Category id is required") Long categoryId) {
}
