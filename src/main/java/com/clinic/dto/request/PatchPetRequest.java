package com.clinic.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** Used by PATCH: every field is optional, but any field that IS sent must be valid. */
public class PatchPetRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(min = 1, max = 50, message = "Species must be between 1 and 50 characters")
    private String species;

    @Size(min = 1, max = 50, message = "Microchip number must be between 1 and 50 characters")
    private String microChipNumber;

    @Min(value = 1, message = "Owner id must be at least 1")
    private Long ownerId;

    @Email(message = "Invalid email format")
    private String contactEmail;

    public PatchPetRequest() {
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
