package com.store.app.customer.dto;

import java.time.Instant;
import java.time.LocalDate;

public class CustomerResponseDto {

	private final Long id;
	private final String customerCode;
	private final String firstName;
	private final String lastName;
	private final String email;
	private final String phone;
	private final String taxNumber;
	private final LocalDate birthDate;
	private final boolean active;
	private final Instant createdAt;
	private final Instant updatedAt;

	public CustomerResponseDto(Long id, String customerCode, String firstName, String lastName,
			String email, String phone, String taxNumber, LocalDate birthDate, boolean active,
			Instant createdAt, Instant updatedAt) {
		this.id = id;
		this.customerCode = customerCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.taxNumber = taxNumber;
		this.birthDate = birthDate;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public boolean isActive() {
		return active;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
