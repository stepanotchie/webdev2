package com.week9.activity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String species;

    @Column(unique = true)
    private String microChipNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @OneToMany(mappedBy = "pet", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecords = new ArrayList<>();

    public Pet() {
    }

    public Pet(String name, String species, String microChipNumber) {
        this.name = name;
        this.species = species;
        this.microChipNumber = microChipNumber;
    }

    public VaccinationRecord vaccinate(Vaccine vaccine, java.time.LocalDate date, int doseNumber) {
        VaccinationRecord record = new VaccinationRecord(date, doseNumber);
        record.setPet(this);
        record.setVaccine(vaccine);
        vaccinationRecords.add(record);
        vaccine.getRecords().add(record);
        return record;
    }

    public void removeVaccinationRecord(VaccinationRecord record) {
        vaccinationRecords.remove(record);
        if (record.getVaccine() != null) {
            record.getVaccine().getRecords().remove(record);
        }
        record.setPet(null);
        record.setVaccine(null);
    }

    public Long getId() {
        return id;
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

    public AppUser getOwner() {
        return owner;
    }

    void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public List<VaccinationRecord> getVaccinationRecords() {
        return vaccinationRecords;
    }

    @Override
    public String toString() {
        return "Pet{id=" + id + ", name=" + name + ", species=" + species + "}";
    }
}
