package com.store.app.product.entity;

import com.store.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_origins")
@Getter
@Setter
public class ProductOrigin extends BaseEntity {

	@Column(nullable = false, length = 100)
	private String country;

	@Column(length = 100)
	private String city;

	@Column(name = "factory_name")
	private String factoryName;

	@Column(name = "factory_code", length = 100)
	private String factoryCode;

}
