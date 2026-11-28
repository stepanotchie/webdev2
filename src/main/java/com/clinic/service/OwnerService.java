package com.clinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.response.OwnerDTO;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.AppUser;
import com.clinic.repository.AppUserRepository;

@Service
@Transactional(readOnly = true)
public class OwnerService {

    private static final int MAX_PAGE_SIZE = 100;

    private final AppUserRepository appUserRepository;

    public OwnerService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Page<OwnerDTO> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return appUserRepository
                .findAll(PageRequest.of(safePage, safeSize, Sort.by("username").ascending()))
                .map(this::toDTO);
    }

    public OwnerDTO findById(Long id) {
        return appUserRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
    }

    private OwnerDTO toDTO(AppUser u) {
        return new OwnerDTO(u.getId(), u.getUsername(), u.getEmail(), u.getCreatedAt());
    }
}
