package com.store.app.product.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.product.dto.ProductRequest;
import com.store.app.product.dto.ProductResponse;
import com.store.app.product.entity.Product;
import com.store.app.product.mapper.ProductMapper;
import com.store.app.product.repository.ManufacturerRepository;
import com.store.app.product.repository.ProductCategoryRepository;
import com.store.app.product.repository.ProductOriginRepository;
import com.store.app.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	// Upper bound of the NUMERIC(12,2) price column
	private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999.99");

	private final ProductRepository productRepository;

	private final ManufacturerRepository manufacturerRepository;

	private final ProductCategoryRepository categoryRepository;

	private final ProductOriginRepository originRepository;

	private final ProductMapper productMapper;

	public List<ProductResponse> search(String name, BigDecimal minPrice, BigDecimal maxPrice) {
		return productRepository
			.findByNameContainingIgnoreCaseAndPriceBetweenOrderByName(name != null ? name.trim() : "",
					minPrice != null ? minPrice : BigDecimal.ZERO, maxPrice != null ? maxPrice : MAX_PRICE)
			.stream()
			.map(productMapper::toResponse)
			.toList();
	}

	public ProductResponse findById(Long id) {
		return productMapper.toResponse(getProduct(id));
	}

	@Transactional
	public ProductResponse create(ProductRequest request) {
		if (productRepository.existsBySku(request.sku())) {
			throw skuAlreadyExists(request.sku());
		}
		Product product = new Product();
		applyRequest(request, product);
		return productMapper.toResponse(productRepository.save(product));
	}

	@Transactional
	public ProductResponse update(Long id, ProductRequest request) {
		Product product = getProduct(id);
		if (productRepository.existsBySkuAndIdNot(request.sku(), id)) {
			throw skuAlreadyExists(request.sku());
		}
		applyRequest(request, product);
		// Flush so the response carries the refreshed updatedAt
		return productMapper.toResponse(productRepository.saveAndFlush(product));
	}

	@Transactional
	public void delete(Long id) {
		productRepository.delete(getProduct(id));
	}

	private Product getProduct(Long id) {
		return productRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found"));
	}

	private void applyRequest(ProductRequest request, Product product) {
		productMapper.update(request, product);
		product.setManufacturer(manufacturerRepository.findById(request.manufacturerId())
			.orElseThrow(() -> unknownReference("Manufacturer", request.manufacturerId())));
		product.setCategory(categoryRepository.findById(request.categoryId())
			.orElseThrow(() -> unknownReference("Category", request.categoryId())));
		product.setOrigin(request.originId() == null ? null
				: originRepository.findById(request.originId())
					.orElseThrow(() -> unknownReference("Origin", request.originId())));
	}

	private static ResponseStatusException skuAlreadyExists(String sku) {
		return new ResponseStatusException(HttpStatus.CONFLICT, "Product with SKU " + sku + " already exists");
	}

	private static ResponseStatusException unknownReference(String type, Long id) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, type + " " + id + " not found");
	}

}
