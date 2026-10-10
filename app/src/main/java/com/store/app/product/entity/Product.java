package com.store.app.product.entity;

import java.math.BigDecimal;

import com.store.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {

	@Column(nullable = false, unique = true, length = 50)
	private String sku;

	@Column(length = 50)
	private String barcode;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "manufacturer_id", nullable = false)
	private Manufacturer manufacturer;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private ProductCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "origin_id")
	private ProductOrigin origin;

	@Column(nullable = false)
	private String name;

	@Column(name = "short_description", length = 500)
	private String shortDescription;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@Column(precision = 10, scale = 3)
	private BigDecimal weight;

	@Column(nullable = false)
	private boolean active = true;

}
