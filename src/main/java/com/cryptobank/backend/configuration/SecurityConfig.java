package com.cryptobank.backend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        //Customer
        RequestMatcher customerPost = new AntPathRequestMatcher("/api/v1/customers", "POST");
        RequestMatcher customerPut = new AntPathRequestMatcher("/api/v1/customers/**", "PUT");
        RequestMatcher customerDelete = new AntPathRequestMatcher("/api/v1/customers/**", "DELETE");

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(request -> {
//                    request.requestMatchers("/").permitAll()
//                            .requestMatchers(customerPost, customerPut, customerDelete).hasAnyAuthority("perm_admin_access", "perm_api_control", "perm_user_management")
//                            .requestMatchers(HttpMethod.GET).permitAll()
//                            .anyRequest().authenticated();
//                })
//                .httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(request -> {
                    request.anyRequest().permitAll();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

}
