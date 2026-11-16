package com.example.projectdnd.security;

import com.example.projectdnd.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security configuration class providing core authentication beans,
 * including user lookup, password hashing, and authentication management.
 */
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Constructs an {@code ApplicationConfig} with the given {@link UserRepository}.
     *
     * @param userRepository repository used to load user details from the database
     */
    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. How Spring Security finds our User in PostgreSQL
    /**
     * Defines how Spring Security loads a user's credentials and authorities from PostgreSQL.
     *
     * @return a {@link UserDetailsService} implementation backed by {@link UserRepository}
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole() != null ? user.getRole() : "USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // 2. Password Encoder using BCrypt
    /**
     * Provides a {@link BCryptPasswordEncoder} bean for hashing and verifying user passwords.
     *
     * @return the configured {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 3. Provider connecting UserDetailsService and PasswordEncoder
    /**
     * Configures the data access authentication provider that links the {@link UserDetailsService}
     * with the {@link PasswordEncoder}.
     *
     * @return the configured {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 4. AuthenticationManager to handle login credentials verification
    /**
     * Exposes the Spring Security {@link AuthenticationManager} bean used to authenticate login requests.
     *
     * @param config the Spring authentication configuration
     * @return the {@link AuthenticationManager} instance
     * @throws Exception if the authentication manager cannot be retrieved
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}