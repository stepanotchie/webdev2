package com.clinic.dto.response;

public class PetDTO {
    private Long id;
    private String name;
    private String species;
    private String microChipNumber;
    private Long ownerId;
    private String contactEmail;

    public PetDTO() {
    }

    public PetDTO(Long id, String name, String species, String microChipNumber, Long ownerId,
            String contactEmail) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.microChipNumber = microChipNumber;
        this.ownerId = ownerId;
        this.contactEmail = contactEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
