package com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds a few pets (only when the pet table is empty) so that pagination,
 * sorting and filtering can be demonstrated and tested.
 */
@Component
public class PetDataSeeder implements CommandLineRunner {

    private final PetRepository petRepository;
    private final AppUserRepository appUserRepository;

    public PetDataSeeder(PetRepository petRepository, AppUserRepository appUserRepository) {
        this.petRepository = petRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void run(String... args) {
        if (petRepository.count() > 0) {
            return;
        }
        AppUser owner = appUserRepository.findByUsername("seed-owner")
                .orElseGet(() -> new AppUser("seed-owner", "seed-owner@example.com"));
        owner.addPet(new Pet("Buddy", "Dog", "MC-1001"));
        owner.addPet(new Pet("Luna", "Cat", "MC-1002"));
        owner.addPet(new Pet("Max", "Dog", "MC-1003"));
        owner.addPet(new Pet("Milo", "Cat", "MC-1004"));
        owner.addPet(new Pet("Rocky", "Dog", "MC-1005"));
        owner.addPet(new Pet("Bella", "Rabbit", "MC-1006"));
        appUserRepository.save(owner);
    }
}
