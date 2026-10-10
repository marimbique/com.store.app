package com.store.app.product.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.store.app.product.dto.ProductRequest;
import com.store.app.product.dto.ProductResponse;
import com.store.app.product.entity.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

	@Mapping(target = "manufacturerId", source = "manufacturer.id")
	@Mapping(target = "manufacturerName", source = "manufacturer.name")
	@Mapping(target = "categoryId", source = "category.id")
	@Mapping(target = "categoryName", source = "category.name")
	@Mapping(target = "originId", source = "origin.id")
	@Mapping(target = "originCountry", source = "origin.country")
	ProductResponse toResponse(Product product);

	// Associations are resolved from their ids by the service
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "manufacturer", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "origin", ignore = true)
	@Mapping(target = "active", defaultValue = "true")
	void update(ProductRequest request, @MappingTarget Product product);

}
