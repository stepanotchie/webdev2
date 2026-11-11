package com.week8.vetclinic;

import com.week8.vetclinic.repository.PetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PetRepository pets;

    public DataSeeder(PetRepository pets) {
        this.pets = pets;
    }

    @Override
    public void run(String... args) {
        seed("985112000000001", "Muffin", "Dog", "Shih Tzu");
        seed("985112000000002", "Brownie", "Dog", "Labrador Retriever");
        seed("985112000000003", "Biscotti", "Cat", "Puspin");
        seed("985112000000004", "Cupcake", "Cat", "Persian");
        seed("985112000000005", "Mochi", "Rabbit", "Holland Lop");
        seed("985112000000006", "Pandesal", "Dog", "Aspin");
    }

    private void seed(String chip, String name, String species, String breed) {
        if (!pets.existsByMicrochipNumber(chip)) {
            pets.save(new Pet(chip, name, species, breed));
        }
    }
}
