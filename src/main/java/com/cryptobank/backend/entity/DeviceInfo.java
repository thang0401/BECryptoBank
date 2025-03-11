package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_info")
public class DeviceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false)
    private String deviceId; // Session ID hoặc mã thiết bị

    @Column(name = "device_name", nullable = false)
    private String deviceName; // Model điện thoại hoặc máy tính

    @Column(name = "os", nullable = false)
    private String os; // Hệ điều hành (Android, iOS, Windows)

    @Column(name = "browser", nullable = false)
    private String browser; // Trình duyệt (Chrome, Firefox, Edge)

    @Column(name = "ip_address", nullable = false)
    private String ipAddress; // Địa chỉ IP

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin; // Thời gian đăng nhập cuối

    @Column(name = "uuid_id", unique = true, nullable = false)
    private String uuidId = UUID.randomUUID().toString(); // Mã UUID duy nhất cho từng thiết bị
    
    @Column(name="in_use")
    private Boolean inUse; // dùng để kiểm tra hiện tại tài khoản đang được hoạt động ở thiết bị nào
    
    @Column(name="otp")
    private String otp;
}
