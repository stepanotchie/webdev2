package com.catalog.service;

import com.catalog.dto.CategoryRequestDTO;
import com.catalog.dto.CategoryResponseDTO;
import com.catalog.exception.ConflictException;
import com.catalog.exception.ResourceNotFoundException;
import com.catalog.model.Category;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream().map(CategoryResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(Long id) {
        return CategoryResponseDTO.from(getEntity(id));
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        Category category = new Category(request.name(), request.description());
        // saveAndFlush so a duplicate name fails right here ->
        // DataIntegrityViolationException -> 409
        return CategoryResponseDTO.from(categoryRepository.saveAndFlush(category));
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
        Category category = getEntity(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return CategoryResponseDTO.from(categoryRepository.saveAndFlush(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = getEntity(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException("Category " + id
                    + " cannot be deleted because it still has products. Delete or move them first.");
        }
        categoryRepository.delete(category);
    }

    private Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}
