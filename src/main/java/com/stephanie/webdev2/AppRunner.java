package com.stephanie.webdev2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private final ProductService productService;

    public AppRunner(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        double threshold = 5000;
        List<Product> filteredProducts = productService.getProductsGreaterThanPrice(threshold);
        String currency = productService.getShopProperties().getCurrency();
        String shopName = productService.getShopProperties().getName();

        System.out.println("================================");
        System.out.println("       PRODUCT REPORT");
        System.out.println("================================");
        System.out.println("Shop: " + shopName);
        System.out.println("Currency: " + currency);
        System.out.println();
        System.out.println("Products above " + currency + " " + (int) threshold + ":");
        System.out.println();

        for (Product product : filteredProducts) {
            System.out.println(product.getName() + " - " + currency + " " + (int) product.getPrice());
        }

        System.out.println("================================");
    }
}