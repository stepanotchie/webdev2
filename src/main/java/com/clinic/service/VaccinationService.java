package com.clinic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.response.VaccinationRecordDTO;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.repository.PetRepository;
import com.clinic.repository.VaccinationRecordRepository;

@Service
@Transactional(readOnly = true)
public class VaccinationService {

    private final VaccinationRecordRepository recordRepository;
    private final PetRepository petRepository;

    public VaccinationService(VaccinationRecordRepository recordRepository, PetRepository petRepository) {
        this.recordRepository = recordRepository;
        this.petRepository = petRepository;
    }

    /** Vaccination history of one pet. An unknown pet is a 404; a pet with no records is an empty list (200). */
    public List<VaccinationRecordDTO> findByPet(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with id: " + petId);
        }
        return recordRepository.findHistoryByPetId(petId).stream()
                .map(r -> new VaccinationRecordDTO(
                        r.getId(),
                        petId,
                        r.getVaccine().getId(),
                        r.getVaccine().getName(),
                        r.getDateGiven(),
                        r.getDoseNumber()))
                .toList();
    }
}
