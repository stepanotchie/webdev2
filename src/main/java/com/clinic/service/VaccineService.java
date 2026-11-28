package com.clinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.response.VaccineDTO;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.Vaccine;
import com.clinic.repository.VaccineRepository;

@Service
@Transactional(readOnly = true)
public class VaccineService {

    private static final int MAX_PAGE_SIZE = 100;

    private final VaccineRepository vaccineRepository;

    public VaccineService(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    public Page<VaccineDTO> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return vaccineRepository
                .findAll(PageRequest.of(safePage, safeSize, Sort.by("name").ascending()))
                .map(this::toDTO);
    }

    public VaccineDTO findById(Long id) {
        return vaccineRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine not found with id: " + id));
    }

    private VaccineDTO toDTO(Vaccine v) {
        return new VaccineDTO(v.getId(), v.getName(), v.getStockDoses());
    }
}
