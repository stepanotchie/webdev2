package com.stephanie.webdev2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@SpringBootApplication
public class RecipeManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeManagerApplication.class, args);
    }

    @Controller
    static class RootRedirectController {
        @GetMapping("/")
        public RedirectView redirectToRecipes() {
            return new RedirectView("/recipes");
        }
    }
}
