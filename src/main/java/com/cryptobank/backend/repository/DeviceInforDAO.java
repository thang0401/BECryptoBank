package com.cryptobank.backend.repository;

import java.util.List;

import java.util.Optional;

import com.cryptobank.backend.entity.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;


public interface DeviceInforDAO extends JpaRepository<DeviceInfo, String> {

	 Optional<DeviceInfo> findByDeviceIdAndUser(String deviceId, User user);
	 
	 Optional<DeviceInfo> findByUser(User user);
	 
	 Optional<DeviceInfo> findByUserId(String userId);
	 
	 @Query("SELECT d FROM DeviceInfo d WHERE d.user.id = :userId AND d.inUse = true")
	 Optional<DeviceInfo> findByUserAndDeviceInUse(@Param("userId") String userId);
	 
	 @Query("SELECT d FROM DeviceInfo d WHERE d.user.id = :userId")
	 List<Optional<DeviceInfo>> getAllDeviceWasLoginByUser(@Param("userId") String userId);

	 List<DeviceInfo> findAllByUserAndDeviceIdNot(User user, String deviceId);
	 
	 List<DeviceInfo> findAllByUser(User user);
	 
	 

	 @Query("SELECT d FROM DeviceInfo d WHERE d.deviceName = :deviceName AND d.browser =:browser AND d.os=:os AND d.user.id=:userId")
	 Optional<DeviceInfo> findByInforOfDevice(@Param("deviceName") String deviceName,@Param("browser") String browser,@Param("os") String os,@Param("userId") String userId);

	@Query("SELECT d FROM DeviceInfo d WHERE d.deviceName = :currentDeviceName AND d.browser =:currentBrowser AND d.os=:currentOs")
	Optional<DeviceInfo> findByInforOfDevice(String currentDeviceName, String currentBrowser, String currentOs);
}
