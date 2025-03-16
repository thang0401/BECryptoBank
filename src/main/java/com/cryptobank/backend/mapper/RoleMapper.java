package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.RoleCreateRequest;
import com.cryptobank.backend.DTO.RoleUpdateRequest;
import com.cryptobank.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    Role fromCreateRequest(RoleCreateRequest request);

    Role fromUpdateRequest(@MappingTarget Role role, RoleUpdateRequest request);

}
