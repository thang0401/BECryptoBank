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
    uses = ConvertStringStrip.class
)
public interface RoleUrlMapper {

    @Mapping(target = "role.id", source = "roleId")
    RoleUrl fromCreateRequest(RoleUrlCreateRequest request);

    @Mapping(target = "role.id", source = "roleId")
    RoleUrl fromUpdateRequest(@MappingTarget RoleUrl roleUrl, RoleUrlUpdateRequest request);

    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "url", source = "functionUrl")
    RoleUrlDTO toDTO(RoleUrl roleUrl);

}
