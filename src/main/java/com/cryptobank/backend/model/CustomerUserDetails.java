package com.cryptobank.backend.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
public class CustomerUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private List<? extends GrantedAuthority> authorities;

    public CustomerUserDetails(String username, List<String> roles) {
        this.username = username;
        this.password = "";
        this.authorities = roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }

}
