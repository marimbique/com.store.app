package com.store.app.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(

		@NotBlank @Size(max = 50) String sku,

		@Size(max = 50) String barcode,

		@NotNull Long manufacturerId,

		@NotNull Long categoryId,

		Long originId,

		@NotBlank @Size(max = 255) String name,

		@Size(max = 500) String shortDescription,

		String description,

		@NotNull @DecimalMin("0") @Digits(integer = 10, fraction = 2) BigDecimal price,

		@DecimalMin("0") @Digits(integer = 7, fraction = 3) BigDecimal weight,

		// Optional; defaults to true when omitted
		Boolean active) {

}
