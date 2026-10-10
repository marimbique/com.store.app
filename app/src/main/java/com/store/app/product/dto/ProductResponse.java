package com.store.app.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(

		Long id,

		String sku,

		String barcode,

		Long manufacturerId,

		String manufacturerName,

		Long categoryId,

		String categoryName,

		Long originId,

		String originCountry,

		String name,

		String shortDescription,

		String description,

		BigDecimal price,

		BigDecimal weight,

		boolean active,

		Instant createdAt,

		Instant updatedAt) {

}
