package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.ReferralBonusDTO;
import com.cryptobank.backend.DTO.request.ReferralBonusCreateRequest;
import com.cryptobank.backend.DTO.request.ReferralBonusUpdateRequest;
import com.cryptobank.backend.entity.ReferralBonus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertStringStrip.class
)
public interface ReferralBonusMapper {

    @Mapping(target = "user", source = "user.email")
    @Mapping(target = "referralUser", source = "referralUser.email")
    @Mapping(target = "status", source = "status.name")
    ReferralBonusDTO toDTO(ReferralBonus employee);

    ReferralBonus fromCreateRequest(ReferralBonusCreateRequest request);

    ReferralBonus fromUpdateRequest(@MappingTarget ReferralBonus found, ReferralBonusUpdateRequest request);

}
