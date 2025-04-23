package com.cryptobank.backend.services;

import com.cryptobank.backend.model.CustomerUserDetails;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.UserRoleDAO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;
    private final UserRoleDAO userRoleDAO;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userDAO.existsById(username)) {
            List<String> roles = userRoleDAO.findRoleNameByUserId(username);
            return new CustomerUserDetails(username, roles);
        } else {
            throw new UsernameNotFoundException("User not found " + username);
        }
    }
}
