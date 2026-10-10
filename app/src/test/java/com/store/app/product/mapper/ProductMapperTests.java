package com.store.app.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.store.app.product.dto.ProductRequest;
import com.store.app.product.dto.ProductResponse;
import com.store.app.product.entity.Manufacturer;
import com.store.app.product.entity.Product;
import com.store.app.product.entity.ProductCategory;
import com.store.app.product.entity.ProductOrigin;

class ProductMapperTests {

	private final ProductMapper mapper = new ProductMapperImpl();

	@Test
	void flattensAssociationsIntoResponse() {
		Product product = product();
		ProductOrigin origin = new ProductOrigin();
		origin.setId(30L);
		origin.setCountry("Portugal");
		product.setOrigin(origin);

		ProductResponse response = mapper.toResponse(product);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.sku()).isEqualTo("SKU-1");
		assertThat(response.price()).isEqualByComparingTo("19.99");
		assertThat(response.manufacturerId()).isEqualTo(10L);
		assertThat(response.manufacturerName()).isEqualTo("Acme");
		assertThat(response.categoryId()).isEqualTo(20L);
		assertThat(response.categoryName()).isEqualTo("Laptops");
		assertThat(response.originId()).isEqualTo(30L);
		assertThat(response.originCountry()).isEqualTo("Portugal");
	}

	@Test
	void leavesOriginFieldsNullWhenProductHasNoOrigin() {
		ProductResponse response = mapper.toResponse(product());

		assertThat(response.originId()).isNull();
		assertThat(response.originCountry()).isNull();
	}

	@Test
	void updateCopiesScalarFieldsAndKeepsIdAndAssociations() {
		Product product = product();
		Manufacturer manufacturer = product.getManufacturer();

		mapper.update(new ProductRequest("SKU-2", "123", 99L, 98L, 97L, "Mouse", "short", "long",
				new BigDecimal("5.00"), new BigDecimal("0.100"), false), product);

		assertThat(product.getSku()).isEqualTo("SKU-2");
		assertThat(product.getBarcode()).isEqualTo("123");
		assertThat(product.getName()).isEqualTo("Mouse");
		assertThat(product.getShortDescription()).isEqualTo("short");
		assertThat(product.getDescription()).isEqualTo("long");
		assertThat(product.getPrice()).isEqualByComparingTo("5.00");
		assertThat(product.getWeight()).isEqualByComparingTo("0.100");
		assertThat(product.isActive()).isFalse();
		assertThat(product.getId()).isEqualTo(1L);
		assertThat(product.getManufacturer()).isSameAs(manufacturer);
	}

	@Test
	void updateDefaultsActiveToTrueWhenOmitted() {
		Product product = product();
		product.setActive(false);

		mapper.update(new ProductRequest("SKU-1", null, 10L, 20L, null, "Laptop", null, null,
				new BigDecimal("19.99"), null, null), product);

		assertThat(product.isActive()).isTrue();
	}

	private static Product product() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setId(10L);
		manufacturer.setName("Acme");
		ProductCategory category = new ProductCategory();
		category.setId(20L);
		category.setName("Laptops");

		Product product = new Product();
		product.setId(1L);
		product.setSku("SKU-1");
		product.setName("Laptop");
		product.setPrice(new BigDecimal("19.99"));
		product.setManufacturer(manufacturer);
		product.setCategory(category);
		return product;
	}

}
