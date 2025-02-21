package com.cryptobank.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

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
    @JoinColumn(name = "userid")
    private User user;

    @Column(name = "deviceid")
    private String deviceId;

    @Column(name = "devicename")
    private String deviceName;

    @Column(name="os")
    private String os;

    @Column(name="browser")
    private String browser;

    @Column(name = "ipaddress")
    private String ipAddress;

    @Column(name = "lastlogin")
    private ZonedDateTime lastLogin;

}
