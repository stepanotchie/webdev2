package com.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(

                @NotBlank(message = "Category name is required") @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters") String name,

                @Size(max = 255, message = "Description must not exceed 255 characters") String description) {
}
