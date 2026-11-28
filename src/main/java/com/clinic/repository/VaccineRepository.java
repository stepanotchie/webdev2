package com.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinic.model.Vaccine;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
}