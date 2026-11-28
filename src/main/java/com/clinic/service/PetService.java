package com.clinic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.request.CreatePetRequest;
import com.clinic.dto.request.PatchPetRequest;
import com.clinic.dto.request.UpdatePetRequest;
import com.clinic.dto.response.PetDTO;
import com.clinic.exception.DuplicateResourceException;
import com.clinic.exception.InvalidRequestException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.model.AppUser;
import com.clinic.model.Pet;
import com.clinic.repository.AppUserRepository;
import com.clinic.repository.PetRepository;

@Service
@Transactional
public class PetService {

    // Only these fields may be used for sorting (prevents a 500 from an unknown field name)
    private static final List<String> SORT_FIELDS = List.of("id", "name", "species", "microChipNumber");

    private final PetRepository petRepository;
    private final AppUserRepository appUserRepository;

    public PetService(PetRepository petRepository, AppUserRepository appUserRepository) {
        this.petRepository = petRepository;
        this.appUserRepository = appUserRepository;
    }

    public PetDTO toDTO(Pet pet) {
        return new PetDTO(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getMicroChipNumber(),
                pet.getOwner().getId(),
                pet.getContactEmail());
    }

    @Transactional(readOnly = true)
    public Page<PetDTO> findAll(Optional<String> species, int page, int size, String sort, String direction) {
        // Supports both ?sort=name&direction=desc and ?sort=name,desc
        String sortField = sort;
        String sortDir = direction;
        if (sort.contains(",")) {
            String[] parts = sort.split(",");
            sortField = parts[0].trim();
            if (parts.length > 1) {
                sortDir = parts[1].trim();
            }
        }
        if (!SORT_FIELDS.contains(sortField)) {
            throw new InvalidRequestException("Invalid sort field '" + sortField + "'. Allowed: " + SORT_FIELDS);
        }
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new InvalidRequestException("Invalid direction '" + sortDir + "'. Use 'asc' or 'desc'");
        }

        Sort sorting = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);

        if (species.isPresent() && !species.get().isBlank()) {
            return petRepository.findBySpeciesIgnoreCase(species.get(), pageable).map(this::toDTO);
        }
        return petRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PetDTO findById(Long id) {
        return toDTO(getPet(id));
    }

    public PetDTO create(CreatePetRequest request) {
        AppUser owner = findOwner(request.getOwnerId());
        if (petRepository.existsByMicroChipNumber(request.getMicroChipNumber())) {
            throw new DuplicateResourceException(duplicateMessage(request.getMicroChipNumber()));
        }
        Pet pet = new Pet(request.getName(), request.getSpecies(), request.getMicroChipNumber());
        pet.setContactEmail(request.getContactEmail());
        owner.addPet(pet);
        return toDTO(petRepository.save(pet));
    }

    public PetDTO update(Long id, UpdatePetRequest request) {
        Pet pet = getPet(id);
        ensureMicroChipAvailable(request.getMicroChipNumber(), id);
        AppUser newOwner = findOwner(request.getOwnerId());
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setMicroChipNumber(request.getMicroChipNumber());
        pet.setContactEmail(request.getContactEmail());
        moveToOwner(pet, newOwner);
        return toDTO(petRepository.save(pet));
    }

    public PetDTO patch(Long id, PatchPetRequest request) {
        Pet pet = getPet(id);
        ensureMicroChipAvailable(request.getMicroChipNumber(), id);
        AppUser newOwner = request.getOwnerId() != null ? findOwner(request.getOwnerId()) : null;
        if (request.getName() != null) {
            pet.setName(request.getName());
        }
        if (request.getSpecies() != null) {
            pet.setSpecies(request.getSpecies());
        }
        if (request.getMicroChipNumber() != null) {
            pet.setMicroChipNumber(request.getMicroChipNumber());
        }
        if (request.getContactEmail() != null) {
            pet.setContactEmail(request.getContactEmail());
        }
        if (newOwner != null) {
            moveToOwner(pet, newOwner);
        }
        return toDTO(petRepository.save(pet));
    }

    public void delete(Long id) {
        Pet pet = getPet(id);
        pet.getOwner().getPets().remove(pet);
        petRepository.delete(pet);
    }

    private Pet getPet(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));
    }

    private AppUser findOwner(Long ownerId) {
        return appUserRepository.findById(ownerId)
                .orElseThrow(() -> new InvalidRequestException("Owner with id " + ownerId + " does not exist"));
    }

    private void moveToOwner(Pet pet, AppUser newOwner) {
        if (!pet.getOwner().getId().equals(newOwner.getId())) {
            pet.getOwner().removePet(pet);
            newOwner.addPet(pet);
        }
    }

    private void ensureMicroChipAvailable(String microChipNumber, Long id) {
        if (microChipNumber != null && petRepository.existsByMicroChipNumberAndIdNot(microChipNumber, id)) {
            throw new DuplicateResourceException(duplicateMessage(microChipNumber));
        }
    }

    private String duplicateMessage(String microChipNumber) {
        return "A pet with microchip number " + microChipNumber + " already exists";
    }
}
