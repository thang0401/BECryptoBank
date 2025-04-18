package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.StatusDTO;
import com.cryptobank.backend.entity.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertStringStrip.class
)
public interface StatusMapper {

    @Mapping(target = "group", source = "groupStatus.name")
    StatusDTO toDTO(Status status);

}
