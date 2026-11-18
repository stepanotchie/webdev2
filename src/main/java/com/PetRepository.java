package com;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByMicroChipNumber(String microChipNumber);

    boolean existsByMicroChipNumberAndIdNot(String microChipNumber, Long id);
}