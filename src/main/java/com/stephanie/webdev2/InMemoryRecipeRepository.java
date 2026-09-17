package com.stephanie.webdev2;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRecipeRepository implements RecipeRepository {

    private final Map<Long, Recipe> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public InMemoryRecipeRepository() {
        save(new Recipe(null, "Classic Sourdough Loaf", "Bread", 180, 1,
                "Artisanal sourdough with a crisp crust and open crumb."));
        save(new Recipe(null, "Chocolate Fudge Brownies", "Brownie", 45, 12,
                "Rich, fudgy brownies with a shiny crackled top."));
        save(new Recipe(null, "Red Velvet Cupcakes", "Cupcake", 60, 18,
                "Moist red velvet cupcakes with cream cheese frosting."));
    }

    @Override
    public Recipe save(Recipe recipe) {
        Long newId = idGenerator.incrementAndGet();
        recipe.setId(newId);
        storage.put(newId, recipe);
        return recipe;
    }

    @Override
    public Optional<Recipe> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Recipe> findAll() {
        return storage.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
    }

    @Override
    public Recipe update(Long id, Recipe updatedRecipe) {
        if (!storage.containsKey(id)) {
            throw new IllegalArgumentException("Recipe with id " + id + " not found.");
        }
        updatedRecipe.setId(id);
        storage.put(id, updatedRecipe);
        return updatedRecipe;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
