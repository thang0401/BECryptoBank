package com.cryptobank.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptobank.backend.entity.DeviceInfo;

public interface DeviceInforDAO extends JpaRepository<DeviceInfo, Long> {

}
