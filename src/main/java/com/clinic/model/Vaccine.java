package com.clinic.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Doses currently in stock. The F2 vaccination workflow will decrement this atomically.
    @Column(nullable = false)
    private int stockDoses;

    @OneToMany(mappedBy = "vaccine")
    private List<VaccinationRecord> records = new ArrayList<>();

    public Vaccine() {
    }

    public Vaccine(String name) {
        this.name = name;
    }

    public Vaccine(String name, int stockDoses) {
        this.name = name;
        this.stockDoses = stockDoses;
    }

    public int getStockDoses() {
        return stockDoses;
    }

    public void setStockDoses(int stockDoses) {
        this.stockDoses = stockDoses;
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

    public List<VaccinationRecord> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "Vaccine{id=" + id + ", name=" + name + "}";
    }
}