package com.store.app.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.product.entity.ProductOrigin;

public interface ProductOriginRepository extends JpaRepository<ProductOrigin, Long> {

}
