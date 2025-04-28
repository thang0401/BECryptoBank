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

    @Mapping(target = "user.id", source = "user.id")
    @Mapping(target = "user.fullName", source = "user.fullName")
    @Mapping(target = "user.email", source = "user.email")
    @Mapping(target = "referralUser.id", source = "referralUser.id")
    @Mapping(target = "referralUser.fullName", source = "referralUser.fullName")
    @Mapping(target = "referralUser.email", source = "referralUser.email")
    @Mapping(target = "status", source = "status.name")
    ReferralBonusDTO toDTO(ReferralBonus employee);

    ReferralBonus fromCreateRequest(ReferralBonusCreateRequest request);

    ReferralBonus fromUpdateRequest(@MappingTarget ReferralBonus found, ReferralBonusUpdateRequest request);

}
