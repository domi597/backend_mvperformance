package at.htlkaindorf.backend_mwperformence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Global CORS configuration for the application.
 * <p>
 * Allows cross-origin requests from any origin with credentials,
 * supporting all standard HTTP methods and headers. This is required
 * so that the frontend (running on a different port or domain) can
 * communicate with the REST API.
 * </p>
 */
@Configuration
public class CorsConfig {

    /**
     * Registers a {@link CorsFilter} bean that applies the CORS policy to every request.
     *
     * @return a configured {@link CorsFilter} applied to all URL patterns ({@code /**})
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
