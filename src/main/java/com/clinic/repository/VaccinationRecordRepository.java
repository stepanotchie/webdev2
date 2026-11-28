package com.clinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clinic.model.VaccinationRecord;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {

    // JOIN FETCH loads the vaccine in the same query (no N+1 lazy loads while building DTOs)
    @Query("select r from VaccinationRecord r join fetch r.vaccine "
            + "where r.pet.id = :petId order by r.dateGiven asc, r.doseNumber asc")
    List<VaccinationRecord> findHistoryByPetId(@Param("petId") Long petId);
}
