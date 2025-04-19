package com.cryptobank.backend.mapper;

import com.cryptobank.backend.DTO.EmployeeDTO;
import com.cryptobank.backend.DTO.request.EmployeeCreateRequest;
import com.cryptobank.backend.DTO.request.EmployeeUpdateRequest;
import com.cryptobank.backend.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = ConvertStringStrip.class
)
public interface EmployeeMapper {

    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "maritalStatus", source = "maritalStatus.name")
    @Mapping(target = "employmentType", source = "employmentType.type_name")
    @Mapping(target = "fullName", expression = "java(employee.getFirstName() + \" \" + employee.getMiddleName() + \" \" + employee.getLastName())")
    EmployeeDTO toDTO(Employee employee);

    @Mapping(target = "maritalStatus.id", source = "maritalStatusId")
    @Mapping(target = "employmentType.id", source = "employmentTypeId")
    Employee fromCreateRequest(EmployeeCreateRequest request);

    @Mapping(target = "status.id", source = "statusId")
    @Mapping(target = "maritalStatus.id", source = "maritalStatusId")
    @Mapping(target = "employmentType.id", source = "employmentTypeId")
    Employee fromUpdateRequest(@MappingTarget Employee found, EmployeeUpdateRequest request);

}
