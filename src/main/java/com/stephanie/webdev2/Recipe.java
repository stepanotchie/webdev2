package com.stephanie.webdev2;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Recipe {

    private Long id;

    @NotBlank(message = "Recipe name is required.")
    @Size(min = 2, max = 100, message = "Recipe name must be between 2 and 100 characters.")
    private String name;

    @NotBlank(message = "Category is required (e.g., Bread, Cake, Cookie, Cupcake, Muffin).")
    @Size(max = 50, message = "Category must not exceed 50 characters.")
    private String category;

    @NotNull(message = "Preparation time is required.")
    @Min(value = 1, message = "Preparation time must be at least 1 minute.")
    @Max(value = 1440, message = "Preparation time must not exceed 1440 minutes (24 hours).")
    private Integer prepTimeMinutes;

    @NotNull(message = "Servings is required.")
    @Min(value = 1, message = "Servings must be at least 1.")
    @Max(value = 500, message = "Servings must not exceed 500.")
    private Integer servings;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    public Recipe() {
    }

    public Recipe(Long id, String name, String category, Integer prepTimeMinutes,
                  Integer servings, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
        this.servings = servings;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
