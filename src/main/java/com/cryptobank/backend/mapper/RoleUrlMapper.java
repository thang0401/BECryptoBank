package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.RoleUrlDTO;
import com.cryptobank.backend.DTO.request.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.request.RoleUrlUpdateRequest;
import com.cryptobank.backend.entity.RoleUrl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertRoleMapper.class
)
public interface RoleUrlMapper {

    @Mapping(target = "functionUrl", source = "url")
    RoleUrl fromCreateRequest(RoleUrlCreateRequest request);

    RoleUrl fromUpdateRequest(@MappingTarget RoleUrl roleUrl, RoleUrlUpdateRequest request);

    @Mapping(target = "roleName", source = "role")
    RoleUrlDTO toResponse(RoleUrl roleUrl);

}
