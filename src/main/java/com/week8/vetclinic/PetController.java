package com.week8.vetclinic;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet registerPet(@Valid @RequestBody Pet pet) {
        return petService.registerPet(pet);
    }

    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePet(@PathVariable Long id) {
        petService.removePet(id);
    }

    @GetMapping("/species")
    public List<Pet> findBySpecies(@RequestParam String name) {
        return petService.findBySpecies(name);
    }

    @GetMapping("/search")
    public List<Pet> searchByName(@RequestParam String keyword) {
        return petService.searchByName(keyword);
    }

    @GetMapping("/species-list")
    public List<String> listSpecies() {
        return petService.findAllSpecies();
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchRegister(@RequestBody List<@Valid Pet> pets) {
        petService.batchRegister(pets);
    }

}