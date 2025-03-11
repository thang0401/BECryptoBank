package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInforDAO extends JpaRepository<DeviceInfo, String> {

}
