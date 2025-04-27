package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.ReferralBonusDTO;
import com.cryptobank.backend.DTO.request.PageParamRequest;
import com.cryptobank.backend.DTO.request.ReferralBonusCreateRequest;
import com.cryptobank.backend.DTO.request.ReferralBonusSearchParamRequest;
import com.cryptobank.backend.DTO.request.ReferralBonusUpdateRequest;
import com.cryptobank.backend.services.ReferralBonusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/referral_bonus", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Referral Bonus", description = "Mẫ giới thiệu")
@SecurityRequirement(name = "Bearer Authorization")
public class ReferralBonusController {

    private final ReferralBonusService referralBonusService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy danh sách referral bonus",
        description = "Trả về danh sách các referral bonus được phân trang với tham số page và size"
    )
    public PagedModel<ReferralBonusDTO> getAllReferralBonuses(
        ReferralBonusSearchParamRequest request,
        @Valid PageParamRequest page
    ) {
        return new PagedModel<>(referralBonusService.getAll(request, page.toPageable()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy thông tin referral bonus theo id"
    )
    public ReferralBonusDTO getReferralBonusById(
        @Parameter(description = "ID referral bonus") @PathVariable String id
    ) {
        return referralBonusService.toDTOFromId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Tạo mới một referral bonus"
    )
    public ReferralBonusDTO addReferralBonus(@Valid @RequestBody ReferralBonusCreateRequest request) {
        return referralBonusService.save(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Cập nhật thông tin referral bonus theo id"
    )
    public ReferralBonusDTO updateReferralBonus(
        @Parameter(description = "ID referral bonus") @PathVariable String id,
        @Valid @RequestBody ReferralBonusUpdateRequest request
    ) {
        return referralBonusService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Xóa một referral bonus theo id"
    )
    public Boolean deleteReferralBonus(
        @Parameter(description = "ID referral bonus") @PathVariable String id
    ) {
        return referralBonusService.deleteById(id);
    }

}
