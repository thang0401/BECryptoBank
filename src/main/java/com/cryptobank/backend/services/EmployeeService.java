package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.DTO.EmployeeDTO;
import com.cryptobank.backend.DTO.EmployeeLogin;
import com.cryptobank.backend.DTO.request.EmployeeCreateRequest;
import com.cryptobank.backend.DTO.request.EmployeeSearchParamRequest;
import com.cryptobank.backend.DTO.request.EmployeeUpdateRequest;
import com.cryptobank.backend.entity.Employee;
import com.cryptobank.backend.entity.EmploymentType;
import com.cryptobank.backend.exception.AlreadyExistException;
import com.cryptobank.backend.exception.AuthException;
import com.cryptobank.backend.exception.ResourceNotFoundException;
import com.cryptobank.backend.mapper.EmployeeMapper;
import com.cryptobank.backend.repository.EmployeeDAO;
import com.cryptobank.backend.repository.EmploymentTypeDAO;
import com.cryptobank.backend.utils.JwtUtil;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeDAO dao;
    private final EmployeeMapper mapper;
    private final EmploymentTypeDAO employmentTypeDAO;
    private final StatusService statusService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(EmployeeLogin request) {
        Employee employee = dao.findOne(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("username"), request.getUserName())))
            .orElseThrow(() -> new AuthException("Wrong username or password"));
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new AuthException("Wrong username or password");
        }
        Map<String, ?> claims = new HashMap<>() {{
            put("id", employee.getId());
            put("username", employee.getUsername());
            put("email", employee.getEmail());
            put("avatar", employee.getAvatar());
            put("isChangePass", employee.isChangePass());
            put("role", employee.getRole() != null ? employee.getRole().getName() : null);
            put("url", employee.getRole() != null ? employee.getRole().getRoleUrls() : new ArrayList<>());
        }};
        return jwtUtil.generateToken(claims);
    }

    public Page<EmployeeDTO> getAll(EmployeeSearchParamRequest request, Pageable pageable) {
        Specification<Employee> spec = ignoreDeleted();
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + request.getEmail().toLowerCase() + "%"));
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            spec = spec.and((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (root.get("firstName") != null) {
                    predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + request.getName().toLowerCase() + "%"));
                }
                if (root.get("lastName") != null) {
                    predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + request.getName().toLowerCase() + "%"));
                }
                if (root.get("middleName") != null) {
                    predicates.add(cb.like(cb.lower(root.get("middleName")), "%" + request.getName().toLowerCase() + "%"));
                }
                return !predicates.isEmpty() ? cb.or(predicates.toArray(new Predicate[0])) : cb.conjunction();
            });
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("phoneNumber"), "%" + request.getPhoneNumber() + "%"));
        }
        if (request.getSalary() != null && !request.getSalary().isBlank()) {
            String[] salaryRange = request.getSalary().split("-");
            if (salaryRange.length == 2) {
                BigDecimal min = new BigDecimal(salaryRange[0]);
                BigDecimal max = new BigDecimal(salaryRange[1]);
                spec = spec.and((root, query, cb) -> cb.between(root.get("salary"), min, max));
            } else if (salaryRange.length == 1) {
                BigDecimal value = new BigDecimal(salaryRange[0]);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("salary"), value));
            }
        }
        if (request.getTaxCode() != null && !request.getTaxCode().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("taxCode"), "%" + request.getTaxCode() + "%"));
        }
        if (request.getEmergencyContactName() != null && !request.getEmergencyContactName().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("emergencyContactName")), "%" + request.getEmergencyContactName().toLowerCase() + "%"));
        }
        if (request.getEmergencyContactPhone() != null && !request.getEmergencyContactPhone().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("emergencyContactPhone"), "%" + request.getEmergencyContactPhone() + "%"));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status").get("id"), request.getStatus()));
        }
        if (request.getMaritalStatus() != null && !request.getMaritalStatus().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("maritalStatus").get("id"), request.getMaritalStatus()));
        }
        if (request.getEmploymentType() != null && !request.getEmploymentType().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("employmentType").get("id"), request.getEmploymentType()));
        }
        return dao.findAll(spec, pageable).map(mapper::toDTO);
    }

    public EmployeeDTO toDTOFromId(String id) {
        Employee employee = getById(id);
        return employee == null ? null : mapper.toDTO(employee);
    }

    public Employee getById(String id) {
        return dao.findOne(ignoreDeleted()
                .and((root, query, cb) -> cb.equal(root.get("id"), id)))
            .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
    }

    public EmployeeDTO save(EmployeeCreateRequest request) {
        boolean found = dao.exists(ignoreDeleted()
            .and((root, query, cb) -> cb.equal(root.get("email"), request.getEmail())));
        if (found) {
            throw new AlreadyExistException("Employee with email " + request.getEmail() + " already exist");
        }
        Employee created = mapper.fromCreateRequest(request);
        String encodePassword = passwordEncoder.encode(created.getPassword());
        created.setPassword(encodePassword);
        if (request.getStatusId() != null && !request.getStatusId().isBlank()) {
            created.setStatus(statusService.getById(request.getStatusId()));
        } else {
            created.setStatus(statusService.getById("cvvvg2rme6nnaun2s4j0"));
        }
        if (request.getMaritalStatusId() != null && !request.getMaritalStatusId().isBlank()) {
            created.setMaritalStatus(statusService.getById(request.getMaritalStatusId()));
        }
        if (request.getEmploymentTypeId() != null && !request.getEmploymentTypeId().isBlank()) {
            created.setEmploymentType(getEmployeeType(request.getEmploymentTypeId()));
        }
        return mapper.toDTO(dao.save(created));
    }

    public EmployeeDTO update(String id, EmployeeUpdateRequest request) {
        Employee found = getById(id);
        if (request.isSimilar(found)) {
            return mapper.toDTO(found);
        }
        Employee updated = mapper.fromUpdateRequest(found, request);
        if (request.getStatusId() != null && !request.getStatusId().isBlank()) {
            updated.setStatus(statusService.getById(request.getStatusId()));
        }
        if (request.getMaritalStatusId() != null && !request.getMaritalStatusId().isBlank()) {
            updated.setMaritalStatus(statusService.getById(request.getMaritalStatusId()));
        }
        if (request.getEmploymentTypeId() != null && !request.getEmploymentTypeId().isBlank()) {
            updated.setEmploymentType(getEmployeeType(request.getEmploymentTypeId()));
        }
        updated.setModifiedAt(OffsetDateTime.now());
        return mapper.toDTO(dao.save(updated));
    }

    public boolean delete(String id) {
        Employee employee = getById(id);
        if (employee.getDeleted()) {
            return false;
        }
        employee.setDeleted(true);
        employee.setModifiedAt(OffsetDateTime.now());
        dao.save(employee);
        return true;
    }

    public EmploymentType getEmployeeType(String id) {
        return employmentTypeDAO.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employment type with id " + id + " not found"));
    }

    private Specification<Employee> ignoreDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
