package com.store.app.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.store.app.customer.repository.CustomerRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeEach
	void clearCustomers() {
		customerRepository.deleteAll();
	}

	@Test
	void createsCustomerAndMakesItAvailableFromReadEndpoints() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "customerCode": "CUST-001",
						  "firstName": "Ada",
						  "lastName": "Lovelace",
						  "email": "ada@example.com",
						  "phone": "+351 123456789",
						  "taxNumber": "123456789",
						  "birthDate": "1815-12-10"
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.customerCode").value("CUST-001"))
				.andExpect(jsonPath("$.active").value(true))
				.andReturn();

		String location = createResult.getResponse().getHeader("Location");

		mockMvc.perform(get(location))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("ada@example.com"));

		mockMvc.perform(get("/api/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].firstName").value("Ada"));
	}

	@Test
	void rejectsInvalidCustomerData() throws Exception {
		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "customerCode": "CUST-002",
						  "firstName": "Grace",
						  "lastName": "Hopper",
						  "email": "not-an-email"
						}
						"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void returnsNotFoundForUnknownCustomer() throws Exception {
		mockMvc.perform(get("/api/customers/999999"))
				.andExpect(status().isNotFound());
	}
}
