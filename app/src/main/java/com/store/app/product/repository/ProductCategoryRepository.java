package com.store.app.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.product.entity.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

}
