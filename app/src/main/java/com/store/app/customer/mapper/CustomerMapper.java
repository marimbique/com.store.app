package com.store.app.customer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.store.app.customer.dto.CustomerRequestDto;
import com.store.app.customer.dto.CustomerResponseDto;
import com.store.app.customer.entity.Customer;

@Mapper(
		componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

	Customer toEntity(CustomerRequestDto request);

	CustomerResponseDto toResponse(Customer customer);
}
