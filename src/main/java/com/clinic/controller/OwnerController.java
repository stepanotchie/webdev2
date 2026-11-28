package com.clinic.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.response.OwnerDTO;
import com.clinic.service.OwnerService;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public Page<OwnerDTO> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ownerService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public OwnerDTO get(@PathVariable Long id) {
        return ownerService.findById(id);
    }
}
