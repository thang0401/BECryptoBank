package com.cryptobank.backend.services;

import com.cryptobank.backend.model.CustomerUserDetails;
import com.cryptobank.backend.repository.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO repository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomerUserDetails user = repository.authenticate(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found " + username);
        } else {
            return new CustomerUserDetails(username, encoder.encode(user.getPassword()), user.getRole());
        }
    }
}
