package com.cryptobank.backend.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String role;
    private List<? extends GrantedAuthority> authorities = new ArrayList<>();

    public CustomerUserDetails(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
