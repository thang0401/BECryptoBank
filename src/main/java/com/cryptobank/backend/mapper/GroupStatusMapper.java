package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.GroupStatusDTO;
import com.cryptobank.backend.entity.GroupStatus;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertStringStrip.class
)
public interface GroupStatusMapper {

    GroupStatusDTO toDTO(GroupStatus group);

}
