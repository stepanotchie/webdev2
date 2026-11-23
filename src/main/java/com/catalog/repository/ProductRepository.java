package com.catalog.repository;

import com.catalog.model.Product;
import com.catalog.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ---- Derived queries ----
    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    // Paginated + filtered; the entity graph fetches the category in the same query
    // (avoids N+1)
    @EntityGraph(attributePaths = "category")
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);

    // ---- Custom JPQL query with @Param ----
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.status = :status ORDER BY p.name")
    List<Product> findAllByStatus(@Param("status") ProductStatus status);
}
