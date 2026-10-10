package com.store.app.product.entity;

import com.store.app.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "manufacturers")
@Getter
@Setter
public class Manufacturer extends BaseEntity {

	@Column(nullable = false, length = 200)
	private String name;

	@Column(length = 500)
	private String website;

	@Column(name = "support_email")
	private String supportEmail;

	@Column(length = 100)
	private String country;

	@Column(nullable = false)
	private boolean active = true;

}
