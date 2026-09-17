package com.stephanie.webdev2;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // READ - list all recipes
    @GetMapping
    public String listRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        return "recipes/list";
    }

    // CREATE - show blank form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("formAction", "/recipes");
        model.addAttribute("formTitle", "Add New Recipe");
        return "recipes/form";
    }

    // CREATE - handle submission
    @PostMapping
    public String createRecipe(@Valid @ModelAttribute("recipe") Recipe recipe,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/recipes");
            model.addAttribute("formTitle", "Add New Recipe");
            return "recipes/form";
        }
        recipeService.createRecipe(recipe);
        return "redirect:/recipes";
    }

    // UPDATE - show pre-populated form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);
        model.addAttribute("recipe", recipe);
        model.addAttribute("formAction", "/recipes/" + id);
        model.addAttribute("formTitle", "Edit Recipe");
        return "recipes/form";
    }

    // UPDATE - handle submission
    @PostMapping("/{id}")
    public String updateRecipe(@PathVariable Long id,
            @Valid @ModelAttribute("recipe") Recipe recipe,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/recipes/" + id);
            model.addAttribute("formTitle", "Edit Recipe");
            return "recipes/form";
        }
        recipeService.updateRecipe(id, recipe);
        return "redirect:/recipes";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/recipes";
    }
}
