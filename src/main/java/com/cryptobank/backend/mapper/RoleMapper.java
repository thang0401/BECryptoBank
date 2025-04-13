package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.RoleDTO;
import com.cryptobank.backend.DTO.request.RoleCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUpdateRequest;
import com.cryptobank.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertStringStrip.class
)
public interface RoleMapper {

    @Mapping(target = "status.id", source = "statusId")
    Role fromCreateRequest(RoleCreateRequest request);

    @Mapping(target = "status.id", source = "statusId")
    Role fromUpdateRequest(@MappingTarget Role role, RoleUpdateRequest request);

    @Mapping(target = "status", source = "status.name")
    RoleDTO toDTO(Role role);

}
