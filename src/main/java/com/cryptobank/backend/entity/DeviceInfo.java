package com.cryptobank.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

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
