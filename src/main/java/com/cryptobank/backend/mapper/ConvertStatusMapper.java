package com.cryptobank.backend.mapper;

import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.repository.StatusDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConvertStatusMapper {

    private final StatusDAO statusDAO;

    public Status toStatus(String name) {
        return name == null ? null : statusDAO.findByName(name);
    }

    public String toString(Status status) {
        return status == null ? null : status.getName();
    }

}
