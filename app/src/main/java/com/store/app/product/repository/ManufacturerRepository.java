package com.store.app.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.product.entity.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

}
