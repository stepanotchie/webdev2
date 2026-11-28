package com.clinic.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePetRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    private String species;

    @NotBlank(message = "Microchip number is required")
    @Size(max = 50, message = "Microchip number must not exceed 50 characters")
    private String microChipNumber;

    @NotNull(message = "Owner id is required")
    @Min(value = 1, message = "Owner id must be at least 1")
    private Long ownerId;

    @Email(message = "Invalid email format")
    private String contactEmail;

    public CreatePetRequest() {
    }

    public CreatePetRequest(String name, String species, String microChipNumber, Long ownerId) {
        this.name = name;
        this.species = species;
        this.microChipNumber = microChipNumber;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getMicroChipNumber() {
        return microChipNumber;
    }

    public void setMicroChipNumber(String microChipNumber) {
        this.microChipNumber = microChipNumber;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
