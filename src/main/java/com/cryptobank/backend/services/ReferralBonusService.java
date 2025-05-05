package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.ReferralBonusDTO;
import com.cryptobank.backend.DTO.request.ReferralBonusCreateRequest;
import com.cryptobank.backend.DTO.request.ReferralBonusSearchParamRequest;
import com.cryptobank.backend.DTO.request.ReferralBonusUpdateRequest;
import com.cryptobank.backend.entity.ReferralBonus;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.ReferralBonusMapper;
import com.cryptobank.backend.repository.ReferralBonusDAO;
import com.cryptobank.backend.repository.UserDAO;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralBonusService {

    private final ReferralBonusDAO dao;
    private final ReferralBonusMapper mapper;
    private final StatusService statusService;
    private final UserService userService;
    private final UserDAO userDAO;

    public Page<ReferralBonusDTO> getAll(ReferralBonusSearchParamRequest request, Pageable pageable) {
        Specification<ReferralBonus> spec = ignoreDeleted();
        if (request.getUserEmail() != null && !request.getUserEmail().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("user").get("email")), "%" + request.getUserEmail().toLowerCase() + "%"));
        }
        if (request.getUserReferralEmail() != null && !request.getUserReferralEmail().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("referralUser").get("email")), "%" + request.getUserReferralEmail().toLowerCase() + "%"));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status").get("id"), request.getStatus()));
        }
        if (request.getBonus() != null && !request.getBonus().isBlank()) {
            String[] bonusRange = request.getBonus().split("-");
            if (bonusRange.length == 2) {
                Double min = Double.parseDouble(bonusRange[0]);
                Double max = Double.parseDouble(bonusRange[1]);
                spec = spec.and((root, query, cb) -> cb.between(root.get("bonus"), min, max));
            } else if (bonusRange.length == 1) {
                Double value = Double.parseDouble(bonusRange[0]);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("bonus"), value));
            }
        }
        return dao.findAll(spec, pageable).map(mapper::toDTO);
    }

    public ReferralBonusDTO toDTOFromId(String id) {
        ReferralBonus referralBonus = getById(id);
        return mapper.toDTO(referralBonus);
    }

    public ReferralBonus getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("Referral bonus with id " + id + " not found"));
    }

    public ReferralBonusDTO save(ReferralBonusCreateRequest request) {
        boolean found = dao.exists(ignoreDeleted()
            .and((root, query, cb) -> cb.equal(root.get("user").get("id"), request.getUserId())));
        if (found) {
            throw new AlreadyExistException("User with id " + request.getUserId() + " has already entered a referral code of " + request.getUserReferralEmail() + " before");
        }
        User user = userService.getUserEntity(request.getUserId());
        if (user.getEmail().equals(request.getUserReferralEmail())) {
            throw new AlreadyExistException("User with id " + request.getUserId() + " cannot enter a referral code of himself/herself");
        }

        User referralUser = userService.getUserByEmail(request.getUserReferralEmail());

        ReferralBonus created = mapper.fromCreateRequest(request);
        created.setStatus(request.getStatusId() != null && !request.getStatusId().isBlank()
            ? statusService.getById(request.getStatusId())
            : statusService.getById("d04sbnufbfnjccci4svg"));
        created.setUser(user);
        created.setReferralUser(referralUser);
        ReferralBonus save = dao.save(created);

        user.setBonusAmount(user.getBonusAmount().add(save.getBonusAmount()));
        user.setIsReferralCode(true);
        userDAO.save(user);
        return mapper.toDTO(save);
    }

    public ReferralBonusDTO update(String id, ReferralBonusUpdateRequest request) {
        ReferralBonus found = getById(id);
        if (request.isSimilar(found)) {
            return mapper.toDTO(found);
        }
        ReferralBonus updated = mapper.fromUpdateRequest(found, request);
        if (request.getStatusId() != null && !request.getStatusId().isBlank()) {
            updated.setStatus(statusService.getById(request.getStatusId()));
        }
        if (request.getUserId() != null && !request.getUserId().isBlank()) {
            updated.setUser(userService.getUserEntity(request.getUserId()));
        }
        if (request.getUserReferralId() != null && !request.getUserReferralId().isBlank()) {
            updated.setReferralUser(userService.getUserEntity(request.getUserReferralId()));
        }
        updated.setModifiedAt(OffsetDateTime.now());
        return mapper.toDTO(dao.save(updated));
    }

    public boolean deleteById(String id) {
        ReferralBonus referralBonus = getById(id);
        if (referralBonus.getDeleted()) {
            return false;
        }
        referralBonus.setDeleted(true);
        referralBonus.setModifiedAt(OffsetDateTime.now());
        dao.save(referralBonus);
        return true;
    }

    private Specification<ReferralBonus> ignoreDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
