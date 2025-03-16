package com.cryptobank.backend.repository;

<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

=======
import com.cryptobank.backend.entity.DeviceInfo;
>>>>>>> 1dc3679127a9ebd69ba397065a0c251b767ed78b
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

<<<<<<< HEAD
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;

public interface DeviceInforDAO extends JpaRepository<DeviceInfo, Long> {
=======
public interface DeviceInforDAO extends JpaRepository<DeviceInfo, String> {
>>>>>>> 1dc3679127a9ebd69ba397065a0c251b767ed78b

	 Optional<DeviceInfo> findByDeviceIdAndUser(String deviceId, User user);
	 
	 Optional<DeviceInfo> findByUser(User user);
	 
	 Optional<DeviceInfo> findByUserId(String userId);
	 
	 @Query("SELECT d FROM DeviceInfo d WHERE d.user.id = :userId AND d.inUse = true")
	 Optional<DeviceInfo> findByUserAndDeviceInUse(@Param("userId") String userId);
	 
	 @Query("SELECT d FROM DeviceInfo d WHERE d.user.id = :userId")
	 List<Optional<DeviceInfo>> getAllDeviceWasLoginByUser(@Param("userId") String userId);

}
