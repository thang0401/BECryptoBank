package com.cryptobank.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;

public interface DeviceInforDAO extends JpaRepository<DeviceInfo, Long> {

	 Optional<DeviceInfo> findByDeviceIdAndUser(String deviceId, User user);
}
