package com.catalog.controller;

import com.catalog.dto.ProductRequestDTO;
import com.catalog.dto.ProductResponseDTO;
import com.catalog.model.ProductStatus;
import com.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products?page=0&size=2&sort=name,asc&categoryId=1
     * Page size is capped (spring.data.web.pageable.max-page-size and
     * ProductService.MAX_PAGE_SIZE).
     */
    @GetMapping
    public Page<ProductResponseDTO> getAll(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return productService.findAll(categoryId, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/sku/{sku}")
    public ProductResponseDTO getBySku(@PathVariable String sku) {
        return productService.findBySku(sku);
    }

    @GetMapping("/status/{status}")
    public List<ProductResponseDTO> getByStatus(@PathVariable ProductStatus status) {
        return productService.findByStatus(status);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created); // 201 + Location header
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
