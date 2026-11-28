package com.clinic.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.response.VaccinationRecordDTO;
import com.clinic.service.VaccinationService;

@RestController
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @GetMapping("/api/pets/{petId}/vaccinations")
    public List<VaccinationRecordDTO> historyForPet(@PathVariable Long petId) {
        return vaccinationService.findByPet(petId);
    }
}
