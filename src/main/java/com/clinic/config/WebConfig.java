package com.clinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS is a browser rule, not security. Only the origins listed in app.cors.allowed-origins
 * (env CORS_ALLOWED_ORIGINS) may call /api/** from another origin. Same-origin pages need no CORS at all.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebConfig(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
