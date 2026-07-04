package at.htlkaindorf.backend_mwperformence.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory rate limiter for {@code POST /api/auth/login}, to slow down
 * brute-force / credential-stuffing attempts against the login endpoint.
 * Tracks failed request counts per client IP in a fixed time window; once the
 * limit is exceeded within the window, further attempts are rejected with
 * {@code 429 Too Many Requests} until the window resets.
 * <p>
 * This is intentionally lightweight (no extra dependency, no external store) and
 * therefore per-instance only: it resets on restart and does not synchronize
 * across multiple app instances behind a load balancer. For a single-instance
 * deployment (as used here) that is sufficient; a multi-instance deployment
 * should move this state into Redis or a similar shared store instead.
 *
 * @author Nici0211
 */
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/auth/login";

    private static final int MAX_ATTEMPTS = 5;

    private static final long WINDOW_MS = 60_000;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, AttemptWindow> attemptsByIp = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean isLoginRequest = "POST".equalsIgnoreCase(request.getMethod())
                && LOGIN_PATH.equals(request.getServletPath());

        if (!isLoginRequest) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        AttemptWindow window = attemptsByIp.computeIfAbsent(clientIp, ip -> new AttemptWindow());

        if (window.registerAttemptAndCheckLimit(MAX_ATTEMPTS, WINDOW_MS)) {
            writeTooManyRequests(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(WINDOW_MS / 1000));

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                "error", "Zu viele Login-Versuche. Bitte warte kurz und versuche es erneut."
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private static class AttemptWindow {
        private long windowStart = System.currentTimeMillis();
        private int count = 0;

        synchronized boolean registerAttemptAndCheckLimit(int maxAttempts, long windowMs) {
            long now = System.currentTimeMillis();

            if (now - windowStart > windowMs) {
                windowStart = now;
                count = 0;
            }

            count++;
            return count > maxAttempts;
        }
    }
}