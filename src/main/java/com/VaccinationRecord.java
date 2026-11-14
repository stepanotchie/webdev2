package com;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "vaccination_record", uniqueConstraints = @UniqueConstraint(name = "uk_pet_vaccine_dose", columnNames = {
        "pet_id", "vaccine_id", "dose_number" }))
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    private LocalDate dateGiven;

    private int doseNumber;

    public VaccinationRecord() {
    }

    public VaccinationRecord(LocalDate dateGiven, int doseNumber) {
        this.dateGiven = dateGiven;
        this.doseNumber = doseNumber;
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    void setPet(Pet pet) {
        this.pet = pet;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public LocalDate getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(LocalDate dateGiven) {
        this.dateGiven = dateGiven;
    }

    public int getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(int doseNumber) {
        this.doseNumber = doseNumber;
    }

    @Override
    public String toString() {
        return "VaccinationRecord{id=" + id + ", date=" + dateGiven + ", dose=" + doseNumber + "}";
    }
}
