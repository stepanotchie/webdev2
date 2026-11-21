package com;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Week11SmokeRunner implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final Environment env;
    private final HttpClient http = HttpClient.newHttpClient();

    public Week11SmokeRunner(AppUserRepository appUserRepository, Environment env) {
        this.appUserRepository = appUserRepository;
        this.env = env;
    }

    @Override
    public void run(String... args) throws Exception {
        banner("WEEK 10 SMOKE TEST - Pet REST API");

        String port = env.getProperty("local.server.port", "8080");
        String base = "http://localhost:" + port + "/api/pets";

        long stamp = System.currentTimeMillis();
        String chip = "MC-API-" + stamp;
        AppUser owner = appUserRepository.save(new AppUser("api-owner-" + stamp, "owner@example.com"));
        Long ownerId = owner.getId();

        HttpResponse<String> r = send("POST", base, json("Coco", "Dog", chip, ownerId));
        String location = r.headers().firstValue("Location").orElse("");
        System.out.println("[1] POST   -> " + r.statusCode() + " Location=" + location);
        System.out.println("    body: " + r.body());
        check(r.statusCode() == 201, "POST should return 201");
        check(!location.isEmpty(), "POST should return a Location header");
        String petUrl = location;

        r = send("GET", petUrl, null);
        System.out.println("[2] GET    -> " + r.statusCode() + " " + r.body());
        check(r.statusCode() == 200, "GET by id should return 200");

        r = send("PUT", petUrl, json("Coco Jr", "Dog", chip, ownerId));
        System.out.println("[3] PUT    -> " + r.statusCode() + " " + r.body());
        check(r.statusCode() == 200, "PUT should return 200");
        check(r.body().contains("Coco Jr"), "PUT should change the name");

        r = send("PATCH", petUrl, "{\"species\":\"Cat\"}");
        System.out.println("[4] PATCH  -> " + r.statusCode() + " " + r.body());
        check(r.statusCode() == 200, "PATCH should return 200");
        check(r.body().contains("\"Cat\""), "PATCH should change species");
        check(r.body().contains("Coco Jr"), "PATCH should leave name unchanged");

        r = send("POST", base, json("Copycat", "Dog", chip, ownerId));
        System.out.println("[5] POST duplicate chip -> " + r.statusCode() + " " + r.body());
        check(r.statusCode() == 409, "duplicate microchip should return 409");

        r = send("DELETE", petUrl, null);
        System.out.println("[6] DELETE -> " + r.statusCode());
        check(r.statusCode() == 204, "DELETE should return 204");

        r = send("GET", petUrl, null);
        System.out.println("[7] GET deleted id -> " + r.statusCode());
        check(r.statusCode() == 404, "GET after delete should return 404");

        r = send("DELETE", petUrl, null);
        System.out.println("[8] DELETE again -> " + r.statusCode());
        check(r.statusCode() == 404, "second DELETE should return 404");

        System.out.println("WEEK 10 SMOKE TEST PASSED");
    }

    private HttpResponse<String> send(String method, String url, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        if (body != null) {
            builder.header("Content-Type", "application/json")
                    .method(method, BodyPublishers.ofString(body));
        } else {
            builder.method(method, BodyPublishers.noBody());
        }
        return http.send(builder.build(), BodyHandlers.ofString());
    }

    private static String json(String name, String species, String chip, Long ownerId) {
        return "{\"name\":\"" + name + "\",\"species\":\"" + species
                + "\",\"microChipNumber\":\"" + chip + "\",\"ownerId\":" + ownerId + "}";
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException("SMOKE TEST FAILED: " + message);
        }
    }

    private static void banner(String text) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("  " + text);
        System.out.println("==================================================================");
    }
}