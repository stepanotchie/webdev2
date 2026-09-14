package com.stephanie.webdev2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // enables auto-configuration and component scanning
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args); // starts the embedded server
    }
}