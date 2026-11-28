package com.clinic.smoke;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.clinic.model.AppUser;
import com.clinic.repository.AppUserRepository;

/**
 * Opt-in end-to-end check of the running REST API (real HTTP calls against the real MySQL database).
 * It does NOT run on a normal start. Activate it with the "smoke" profile:
 *
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=smoke      (PowerShell: quote the -D argument)
 *   or set the environment variable SPRING_PROFILES_ACTIVE=smoke
 *
 * Part A checks the read endpoints and the empty / not-found cases used by the frontend.
 * Part B runs a full create-read-update-patch-delete lifecycle on a temporary pet.
 * All temporary data (pet and owner) is removed at the end. Any failed check stops the run with an error.
 */
@Profile("smoke")
@Component
public class PetApiSmokeRunner implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final Environment env;
    private final HttpClient http = HttpClient.newHttpClient();
    private int step = 0;

    public PetApiSmokeRunner(AppUserRepository appUserRepository, Environment env) {
        this.appUserRepository = appUserRepository;
        this.env = env;
    }

    @Override
    public void run(String... args) throws Exception {
        banner("PET CLINIC API SMOKE TEST");

        String root = "http://localhost:" + env.getProperty("local.server.port", "8080");
        String pets = root + "/api/pets";

        // ---- Part A: read endpoints, empty result, not-found ----
        HttpResponse<String> r = send("GET", pets + "?page=0&size=5", null);
        report("GET /api/pets", r);
        check(r.statusCode() == 200, "list pets should return 200");
        check(r.body().contains("\"content\""), "list pets should return records inside 'content'");

        r = send("GET", pets + "?species=NoSuchSpecies", null);
        report("GET /api/pets?species=NoSuchSpecies (empty filter)", r);
        check(r.statusCode() == 200, "an empty filter result should still be 200");
        check(r.body().contains("\"content\":[]"), "an empty filter result should have an empty 'content' array");

        expect("GET", pets + "/999999", 404, "unknown pet id");
        expect("GET", pets + "/999999/vaccinations", 404, "vaccination history of an unknown pet");
        expect("GET", root + "/api/owners/999999", 404, "unknown owner id");
        expect("GET", root + "/api/vaccines/999999", 404, "unknown vaccine id");
        expect("GET", root + "/api/owners?page=0&size=5", 200, "owners list");
        expect("GET", root + "/api/vaccines?page=0&size=5", 200, "vaccines list");

        // ---- Part B: CRUD lifecycle on a temporary pet ----
        long stamp = System.currentTimeMillis();
        String chip = "MC-SMOKE-" + stamp;
        AppUser owner = appUserRepository.save(new AppUser("smoke-owner-" + stamp, "smoke-owner@example.com"));
        Long ownerId = owner.getId();
        try {
            r = send("POST", pets, json("Coco", "Dog", chip, ownerId));
            String location = r.headers().firstValue("Location").orElse("");
            report("POST create", r);
            check(r.statusCode() == 201, "POST should return 201");
            check(!location.isEmpty(), "POST should return a Location header");

            r = send("GET", location, null);
            report("GET created pet", r);
            check(r.statusCode() == 200, "GET by id should return 200");

            expect("GET", location + "/vaccinations", 200, "a new pet has an (empty) vaccination history");

            r = send("PUT", location, json("Coco Jr", "Dog", chip, ownerId));
            report("PUT replace", r);
            check(r.statusCode() == 200, "PUT should return 200");
            check(r.body().contains("Coco Jr"), "PUT should change the name");

            r = send("PATCH", location, "{\"species\":\"Cat\"}");
            report("PATCH species only", r);
            check(r.statusCode() == 200, "PATCH should return 200");
            check(r.body().contains("\"Cat\""), "PATCH should change species");
            check(r.body().contains("Coco Jr"), "PATCH should leave the name unchanged");

            r = send("POST", pets, json("Copycat", "Dog", chip, ownerId));
            report("POST duplicate microchip", r);
            check(r.statusCode() == 409, "duplicate microchip should return 409");

            r = send("DELETE", location, null);
            report("DELETE", r);
            check(r.statusCode() == 204, "DELETE should return 204");

            expect("GET", location, 404, "GET after delete");
            expect("DELETE", location, 404, "second DELETE");
        } finally {
            try {
                appUserRepository.deleteById(ownerId); // remove the temporary owner
            } catch (Exception ignored) {
                // a failed check may have left the pet behind; the original failure is the important message
            }
        }

        banner("SMOKE TEST PASSED (" + step + " requests)");
    }

    private void expect(String method, String url, int status, String what) throws Exception {
        HttpResponse<String> r = send(method, url, null);
        report(method + " " + what, r);
        check(r.statusCode() == status, what + " should return " + status + " but returned " + r.statusCode());
    }

    private void report(String label, HttpResponse<String> r) {
        step++;
        String body = r.body() == null ? "" : r.body();
        String shortBody = body.length() > 120 ? body.substring(0, 120) + "..." : body;
        System.out.println("[" + step + "] " + label + " -> " + r.statusCode() + (shortBody.isEmpty() ? "" : "  " + shortBody));
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
