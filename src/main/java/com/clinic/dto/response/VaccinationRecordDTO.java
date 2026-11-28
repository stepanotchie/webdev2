package com.clinic.dto.response;

import java.time.LocalDate;

public class VaccinationRecordDTO {
    private Long id;
    private Long petId;
    private Long vaccineId;
    private String vaccineName;
    private LocalDate dateGiven;
    private int doseNumber;

    public VaccinationRecordDTO() {
    }

    public VaccinationRecordDTO(Long id, Long petId, Long vaccineId, String vaccineName,
            LocalDate dateGiven, int doseNumber) {
        this.id = id;
        this.petId = petId;
        this.vaccineId = vaccineId;
        this.vaccineName = vaccineName;
        this.dateGiven = dateGiven;
        this.doseNumber = doseNumber;
    }

    public Long getId() { return id; }
    public Long getPetId() { return petId; }
    public Long getVaccineId() { return vaccineId; }
    public String getVaccineName() { return vaccineName; }
    public LocalDate getDateGiven() { return dateGiven; }
    public int getDoseNumber() { return doseNumber; }
}
