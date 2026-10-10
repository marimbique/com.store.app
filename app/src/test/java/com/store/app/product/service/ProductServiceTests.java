package com.store.app.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.product.dto.ProductRequest;
import com.store.app.product.dto.ProductResponse;
import com.store.app.product.entity.Manufacturer;
import com.store.app.product.entity.Product;
import com.store.app.product.entity.ProductCategory;
import com.store.app.product.entity.ProductOrigin;
import com.store.app.product.mapper.ProductMapperImpl;
import com.store.app.product.repository.ManufacturerRepository;
import com.store.app.product.repository.ProductCategoryRepository;
import com.store.app.product.repository.ProductOriginRepository;
import com.store.app.product.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ManufacturerRepository manufacturerRepository;

	@Mock
	private ProductCategoryRepository categoryRepository;

	@Mock
	private ProductOriginRepository originRepository;

	private ProductService service;

	private Manufacturer manufacturer;

	private ProductCategory category;

	@BeforeEach
	void setUp() {
		service = new ProductService(productRepository, manufacturerRepository, categoryRepository, originRepository,
				new ProductMapperImpl());

		manufacturer = new Manufacturer();
		manufacturer.setId(10L);
		manufacturer.setName("Acme");
		category = new ProductCategory();
		category.setId(20L);
		category.setName("Laptops");
	}

	@Test
	void searchWithoutFiltersMatchesEveryNameAndPrice() {
		when(productRepository.findByNameContainingIgnoreCaseAndPriceBetweenOrderByName("", BigDecimal.ZERO,
				new BigDecimal("9999999999.99")))
			.thenReturn(List.of(product(1L, "SKU-1")));

		assertThat(service.search(null, null, null)).extracting(ProductResponse::sku).containsExactly("SKU-1");
	}

	@Test
	void searchPassesTrimmedNameAndPriceBounds() {
		service.search("  laptop ", new BigDecimal("100"), new BigDecimal("500"));

		verify(productRepository).findByNameContainingIgnoreCaseAndPriceBetweenOrderByName("laptop",
				new BigDecimal("100"), new BigDecimal("500"));
	}

	@Test
	void findByIdThrowsNotFoundForUnknownProduct() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertStatus(() -> service.findById(1L), HttpStatus.NOT_FOUND);
	}

	@Test
	void createSavesProductWithResolvedAssociations() {
		ProductOrigin origin = new ProductOrigin();
		origin.setId(30L);
		origin.setCountry("Portugal");
		when(manufacturerRepository.findById(10L)).thenReturn(Optional.of(manufacturer));
		when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
		when(originRepository.findById(30L)).thenReturn(Optional.of(origin));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ProductResponse response = service.create(request("SKU-1", 30L));

		assertThat(response.sku()).isEqualTo("SKU-1");
		assertThat(response.manufacturerName()).isEqualTo("Acme");
		assertThat(response.categoryName()).isEqualTo("Laptops");
		assertThat(response.originCountry()).isEqualTo("Portugal");
		assertThat(response.active()).isTrue();
	}

	@Test
	void createRejectsDuplicateSku() {
		when(productRepository.existsBySku("SKU-1")).thenReturn(true);

		assertStatus(() -> service.create(request("SKU-1", null)), HttpStatus.CONFLICT);
		verify(productRepository, never()).save(any());
	}

	@Test
	void createRejectsUnknownManufacturer() {
		when(manufacturerRepository.findById(10L)).thenReturn(Optional.empty());

		assertStatus(() -> service.create(request("SKU-1", null)), HttpStatus.BAD_REQUEST);
		verify(productRepository, never()).save(any());
	}

	@Test
	void createRejectsUnknownCategory() {
		when(manufacturerRepository.findById(10L)).thenReturn(Optional.of(manufacturer));
		when(categoryRepository.findById(20L)).thenReturn(Optional.empty());

		assertStatus(() -> service.create(request("SKU-1", null)), HttpStatus.BAD_REQUEST);
		verify(productRepository, never()).save(any());
	}

	@Test
	void createRejectsUnknownOrigin() {
		when(manufacturerRepository.findById(10L)).thenReturn(Optional.of(manufacturer));
		when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
		when(originRepository.findById(30L)).thenReturn(Optional.empty());

		assertStatus(() -> service.create(request("SKU-1", 30L)), HttpStatus.BAD_REQUEST);
		verify(productRepository, never()).save(any());
	}

	@Test
	void updateAppliesChangesAndClearsOriginWhenOmitted() {
		Product existing = product(1L, "SKU-1");
		existing.setOrigin(new ProductOrigin());
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
		when(manufacturerRepository.findById(10L)).thenReturn(Optional.of(manufacturer));
		when(categoryRepository.findById(20L)).thenReturn(Optional.of(category));
		when(productRepository.saveAndFlush(existing)).thenReturn(existing);

		ProductResponse response = service.update(1L, request("SKU-2", null));

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.sku()).isEqualTo("SKU-2");
		assertThat(existing.getOrigin()).isNull();
	}

	@Test
	void updateRejectsSkuUsedByAnotherProduct() {
		when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, "SKU-1")));
		when(productRepository.existsBySkuAndIdNot("SKU-2", 1L)).thenReturn(true);

		assertStatus(() -> service.update(1L, request("SKU-2", null)), HttpStatus.CONFLICT);
		verify(productRepository, never()).saveAndFlush(any());
	}

	@Test
	void updateThrowsNotFoundForUnknownProduct() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertStatus(() -> service.update(1L, request("SKU-1", null)), HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteRemovesExistingProduct() {
		Product existing = product(1L, "SKU-1");
		when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

		service.delete(1L);

		verify(productRepository).delete(existing);
	}

	@Test
	void deleteThrowsNotFoundForUnknownProduct() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertStatus(() -> service.delete(1L), HttpStatus.NOT_FOUND);
		verify(productRepository, never()).delete(any());
	}

	private static void assertStatus(ThrowingCallable call, HttpStatus expected) {
		assertThatThrownBy(call).isInstanceOfSatisfying(ResponseStatusException.class,
				ex -> assertThat(ex.getStatusCode()).isEqualTo(expected));
	}

	private static ProductRequest request(String sku, Long originId) {
		return new ProductRequest(sku, null, 10L, 20L, originId, "Laptop", null, null, new BigDecimal("19.99"), null,
				null);
	}

	private Product product(Long id, String sku) {
		Product product = new Product();
		product.setId(id);
		product.setSku(sku);
		product.setName("Laptop");
		product.setPrice(new BigDecimal("19.99"));
		product.setManufacturer(manufacturer);
		product.setCategory(category);
		return product;
	}

}
