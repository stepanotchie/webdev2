package com;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PetService {

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
                pet.getOwner().getId());
    }

    @Transactional(readOnly = true)
    public List<PetDTO> findAll() {
        return petRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public Optional<PetDTO> findById(Long id) {
        return petRepository.findById(id).map(this::toDTO);
    }

    public PetDTO create(CreatePetRequest request) {
        AppUser owner = findOwner(request.getOwnerId());
        if (petRepository.existsByMicroChipNumber(request.getMicroChipNumber())) {
            throw new DuplicateResourceException(duplicateMessage(request.getMicroChipNumber()));
        }
        Pet pet = new Pet(request.getName(), request.getSpecies(), request.getMicroChipNumber());
        owner.addPet(pet);
        return toDTO(petRepository.save(pet));
    }

    public Optional<PetDTO> update(Long id, UpdatePetRequest request) {
        return petRepository.findById(id).map(pet -> {
            ensureMicroChipAvailable(request.getMicroChipNumber(), id);
            AppUser newOwner = findOwner(request.getOwnerId());
            pet.setName(request.getName());
            pet.setSpecies(request.getSpecies());
            pet.setMicroChipNumber(request.getMicroChipNumber());
            moveToOwner(pet, newOwner);
            return toDTO(petRepository.save(pet));
        });
    }

    public Optional<PetDTO> patch(Long id, UpdatePetRequest request) {
        return petRepository.findById(id).map(pet -> {
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
            if (newOwner != null) {
                moveToOwner(pet, newOwner);
            }
            return toDTO(petRepository.save(pet));
        });
    }

    public boolean delete(Long id) {
        Optional<Pet> pet = petRepository.findById(id);
        if (pet.isEmpty()) {
            return false;
        }
        pet.get().getOwner().getPets().remove(pet.get());
        petRepository.delete(pet.get());
        return true;
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