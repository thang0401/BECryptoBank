package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.RoleUrlCreateRequest;
import com.cryptobank.backend.DTO.RoleUrlUpdateRequest;
import com.cryptobank.backend.entity.RoleUrl;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleUrlMapper {

    RoleUrl fromCreateRequest(RoleUrlCreateRequest request);

    RoleUrl fromUpdateRequest(@MappingTarget RoleUrl roleUrl, RoleUrlUpdateRequest request);


}
