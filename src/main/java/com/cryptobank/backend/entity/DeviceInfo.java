package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_info")
public class DeviceInfo extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "deviceid", nullable = false)
    private String deviceId; // Session ID hoặc mã thiết bị

    @Column(name = "devicename", nullable = false)
    private String deviceName; // Model điện thoại hoặc máy tính

    @Column(name = "os", nullable = false)
    private String os; // Hệ điều hành (Android, iOS, Windows)

    @Column(name = "browser", nullable = false)
    private String browser; // Trình duyệt (Chrome, Firefox, Edge)

    @Column(name = "ipaddress", nullable = false)
    private String ipAddress; // Địa chỉ IP

    @Column(name = "lastlogin", nullable = false)
    private ZonedDateTime lastLogin; // Thời gian đăng nhập cuối

    @Column(name = "uuid_id", unique = true, nullable = false)
    private String uuidId = UUID.randomUUID().toString(); // Mã UUID duy nhất cho từng thiết bị
}
