package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertStringStrip.class
)
public interface UserMapper {

    UserInformation toDTO(User user);

}
