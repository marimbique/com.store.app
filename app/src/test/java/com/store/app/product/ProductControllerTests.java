package com.store.app.product;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.store.app.product.entity.Manufacturer;
import com.store.app.product.entity.Product;
import com.store.app.product.entity.ProductCategory;
import com.store.app.product.repository.ManufacturerRepository;
import com.store.app.product.repository.ProductCategoryRepository;
import com.store.app.product.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ManufacturerRepository manufacturerRepository;

	@Autowired
	private ProductCategoryRepository categoryRepository;

	private Manufacturer manufacturer;

	private ProductCategory category;

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		manufacturerRepository.deleteAll();
		categoryRepository.deleteAll();

		manufacturer = new Manufacturer();
		manufacturer.setName("Acme");
		manufacturer = manufacturerRepository.save(manufacturer);

		category = new ProductCategory();
		category.setName("Laptops");
		category = categoryRepository.save(category);
	}

	@Test
	void createsAndReadsProduct() throws Exception {
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json("SKU-1", "19.99")))
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"))
			.andExpect(jsonPath("$.sku").value("SKU-1"))
			.andExpect(jsonPath("$.manufacturerName").value("Acme"))
			.andExpect(jsonPath("$.categoryName").value("Laptops"))
			.andExpect(jsonPath("$.active").value(true))
			.andExpect(jsonPath("$.createdAt").exists());

		Long id = productRepository.findAll().get(0).getId();
		mockMvc.perform(get("/api/products/{id}", id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.price").value(19.99));
	}

	@Test
	void searchesByNameAndPriceRange() throws Exception {
		save("SKU-1", "Gaming Laptop", "1500.00");
		save("SKU-2", "Office Laptop", "700.00");
		save("SKU-3", "Mouse", "25.00");

		mockMvc.perform(get("/api/products")).andExpect(jsonPath("$", hasSize(3)));
		mockMvc.perform(get("/api/products").param("name", "laptop"))
			.andExpect(jsonPath("$[*].sku", contains("SKU-1", "SKU-2")));
		mockMvc.perform(get("/api/products").param("maxPrice", "700"))
			.andExpect(jsonPath("$[*].sku", contains("SKU-3", "SKU-2")));
		mockMvc.perform(get("/api/products").param("name", "laptop").param("minPrice", "1000"))
			.andExpect(jsonPath("$[*].sku", contains("SKU-1")));
	}

	@Test
	void updatesAndDeletesProduct() throws Exception {
		Long id = save("SKU-1", "Mouse", "25.00").getId();

		mockMvc
			.perform(put("/api/products/{id}", id).contentType(MediaType.APPLICATION_JSON)
				.content(json("SKU-1", "30.00")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.price").value(30.00));

		mockMvc.perform(delete("/api/products/{id}", id)).andExpect(status().isNoContent());
		mockMvc.perform(get("/api/products/{id}", id)).andExpect(status().isNotFound());
	}

	@Test
	void rejectsInvalidRequests() throws Exception {
		save("SKU-1", "Mouse", "25.00");

		// duplicate SKU
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json("SKU-1", "10.00")))
			.andExpect(status().isConflict());
		// negative price
		mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json("SKU-2", "-1")))
			.andExpect(status().isBadRequest());
		// unknown manufacturer
		mockMvc
			.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("""
					{"sku": "SKU-3", "name": "Keyboard", "price": 10, "manufacturerId": 999999, "categoryId": %d}
					""".formatted(category.getId())))
			.andExpect(status().isBadRequest());
	}

	private Product save(String sku, String name, String price) {
		Product product = new Product();
		product.setSku(sku);
		product.setName(name);
		product.setPrice(new BigDecimal(price));
		product.setManufacturer(manufacturer);
		product.setCategory(category);
		return productRepository.save(product);
	}

	private String json(String sku, String price) {
		return """
				{"sku": "%s", "name": "Keyboard", "price": %s, "manufacturerId": %d, "categoryId": %d}
				""".formatted(sku, price, manufacturer.getId(), category.getId());
	}

}
