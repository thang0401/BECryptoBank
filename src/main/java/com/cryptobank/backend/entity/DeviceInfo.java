package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "device_info")
public class DeviceInfo extends BaseEntity {

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

    @Column(name = "in_use")
    private Boolean inUse = false;

}