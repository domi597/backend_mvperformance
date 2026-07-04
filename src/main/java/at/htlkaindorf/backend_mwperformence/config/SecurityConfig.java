package at.htlkaindorf.backend_mwperformence.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginRateLimitFilter loginRateLimitFilter;

    /**
     * Configures the {@link SecurityFilterChain} with the authorization rules
     * described in the class-level JavaDoc.
     *
     * @param http the {@link HttpSecurity} builder provided by Spring
     * @return the fully configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while building the filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/services/**",
                                "/api/offers/**",
                                "/api/opening-hours/**",
                                "/api/contact-info/**",
                                "/api/reviews/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        // Admin-only: full customer list (used by the admin dashboard)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        // Self-service: a customer may cancel their own appointment (ownership checked in service layer)
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/appointments/*/cancel").authenticated()
                        // Admin-only: arbitrary status changes (accept/decline/complete) on any appointment
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/appointments/*/status").hasRole("ADMIN")
                        // Admin-only: deleting appointments
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/appointments/*").hasRole("ADMIN")
                        // Admin-only: resetting another user's password without knowing the old one
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/*/password").hasRole("ADMIN")
                        // Self-service profile endpoints (view/update/delete own account, change own password)
                        // and admin customer management — both share these routes. Ownership (self vs. ADMIN)
                        // is enforced in UserService for each individual route.
                        .requestMatchers("/api/users/**").authenticated()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a {@link BCryptPasswordEncoder} bean used for hashing and verifying passwords.
     *
     * @return a {@link PasswordEncoder} backed by BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean so it can be injected into
     * services that need to programmatically authenticate users.
     *
     * @param config the Spring-managed {@link AuthenticationConfiguration}
     * @return the application's {@link AuthenticationManager}
     * @throws Exception if an error occurs while retrieving the manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}