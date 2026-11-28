package com.clinic.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.response.VaccineDTO;
import com.clinic.service.VaccineService;

@RestController
@RequestMapping("/api/vaccines")
public class VaccineController {

    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public Page<VaccineDTO> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return vaccineService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public VaccineDTO get(@PathVariable Long id) {
        return vaccineService.findById(id);
    }
}
