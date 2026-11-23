package com.catalog.service;

import com.catalog.dto.ProductRequestDTO;
import com.catalog.dto.ProductResponseDTO;
import com.catalog.exception.BadRequestException;
import com.catalog.exception.ResourceNotFoundException;
import com.catalog.model.Category;
import com.catalog.model.Product;
import com.catalog.model.ProductStatus;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    /** Hard cap on page size, prevents unbounded queries. */
    public static final int MAX_PAGE_SIZE = 50;

    private static final List<String> SORTABLE_FIELDS = List.of("id", "name", "sku", "price", "stockQuantity",
            "status");

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Long categoryId, Pageable pageable) {
        Pageable safe = sanitize(pageable);
        Page<Product> page = (categoryId == null)
                ? productRepository.findAll(safe)
                : productRepository.findByCategoryId(categoryId, safe);
        return page.map(ProductResponseDTO::from); // mapped inside the transaction
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return ProductResponseDTO.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product with SKU " + sku + " was not found"));
        return ProductResponseDTO.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByStatus(ProductStatus status) {
        return productRepository.findAllByStatus(status).stream().map(ProductResponseDTO::from).toList();
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        Category category = getCategory(request.categoryId());
        Product product = new Product();
        apply(product, request, category);
        return ProductResponseDTO.from(productRepository.saveAndFlush(product));
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {
        Product product = getEntity(id);
        Category category = getCategory(request.categoryId());
        apply(product, request, category);
        return ProductResponseDTO.from(productRepository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(getEntity(id));
    }

    // ---------- helpers ----------
    private void apply(Product product, ProductRequestDTO request, Category category) {
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        if (request.status() != null) {
            product.setStatus(request.status()); // otherwise keeps the entity default (ACTIVE)
        }
        product.setCategory(category);
    }

    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private Pageable sanitize(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new BadRequestException("Cannot sort by '" + order.getProperty()
                        + "'. Allowed fields: " + SORTABLE_FIELDS);
            }
        }
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
