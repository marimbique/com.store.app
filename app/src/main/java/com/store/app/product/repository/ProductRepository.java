package com.store.app.product.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	// The entity graphs fetch the lazy associations in the same query, avoiding N+1 when mapping to DTOs.

	@Override
	@EntityGraph(attributePaths = { "manufacturer", "category", "origin" })
	Optional<Product> findById(Long id);

	@EntityGraph(attributePaths = { "manufacturer", "category", "origin" })
	List<Product> findByNameContainingIgnoreCaseAndPriceBetweenOrderByName(String name, BigDecimal minPrice,
			BigDecimal maxPrice);

	boolean existsBySku(String sku);

	boolean existsBySkuAndIdNot(String sku, Long id);

}
