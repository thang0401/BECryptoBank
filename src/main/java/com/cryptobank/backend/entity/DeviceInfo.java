package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "device_info")
public class DeviceInfo extends BaseEntity {

	
	@Id
	@Column(name="id")
	private String id= UUID.randomUUID().toString();
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false, columnDefinition = "TEXT")
    private String deviceId;

    @Column(name = "device_name", columnDefinition = "TEXT")
    private String deviceName;

    @Column(name = "os", length = 100, columnDefinition = "TEXT")
    private String os;

    @Column(name = "browser", length = 100, columnDefinition = "TEXT")
    private String browser;

    @Column(name = "ip_address", length = 45, columnDefinition = "TEXT")
    private String ipAddress;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;


    @Column(name = "uuid_id", unique = true, nullable = false)
    private String uuidId = UUID.randomUUID().toString(); // Mã UUID duy nhất cho từng thiết bị
    
    @Column(name="in_use")
    private Boolean inUse; // dùng để kiểm tra hiện tại tài khoản đang được hoạt động ở thiết bị nào
    
    @Column(name="otp")
    private String otp;
}




