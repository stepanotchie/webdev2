package com;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByMicroChipNumber(String microChipNumber);

    boolean existsByMicroChipNumberAndIdNot(String microChipNumber, Long id);

    // Filter used by GET /api/pets?species=...
    Page<Pet> findBySpeciesIgnoreCase(String species, Pageable pageable);
}
