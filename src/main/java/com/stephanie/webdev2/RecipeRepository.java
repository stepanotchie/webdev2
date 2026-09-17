package com.stephanie.webdev2;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {

    Recipe save(Recipe recipe);

    Optional<Recipe> findById(Long id);

    List<Recipe> findAll();

    Recipe update(Long id, Recipe updatedRecipe);

    void deleteById(Long id);
}
