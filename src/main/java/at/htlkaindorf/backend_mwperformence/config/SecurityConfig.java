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

/**
 * Central Spring Security configuration for the application.
 * <p>
 * Defines the HTTP security rules:
 * <ul>
 *   <li>CSRF protection is disabled because the API is stateless and uses JWT.</li>
 *   <li>Session management is set to {@link SessionCreationPolicy#STATELESS}; no
 *       HTTP session is created or used.</li>
 *   <li>Public endpoints (auth, services, offers, opening hours, contact info,
 *       reviews, Swagger UI, Actuator) are accessible without authentication.</li>
 *   <li>The {@code /api/users/**} endpoints are restricted to users with the
 *       {@code ADMIN} role.</li>
 *   <li>All other endpoints require a valid JWT.</li>
 * </ul>
 * The {@link JwtAuthenticationFilter} is inserted before the default
 * {@link UsernamePasswordAuthenticationFilter} so JWT tokens are evaluated first.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
                // Öffentliche Endpunkte
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
                // Admin-only
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                // Alles andere braucht Login
                .anyRequest().authenticated()
            )
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
     * services that need to programmatically authenticate users (e.g., {@code AuthService}).
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
