package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.config.JwtService;
import at.htlkaindorf.backend_mwperformence.dtos.AuthResponse;
import at.htlkaindorf.backend_mwperformence.dtos.LoginRequest;
import at.htlkaindorf.backend_mwperformence.dtos.RegisterRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.entites.Role;
import at.htlkaindorf.backend_mwperformence.entites.User;
import at.htlkaindorf.backend_mwperformence.entites.Vehicle;
import at.htlkaindorf.backend_mwperformence.exception.ApiException;
import at.htlkaindorf.backend_mwperformence.repositories.UserRepository;
import at.htlkaindorf.backend_mwperformence.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Service responsible for user authentication and registration.
 * Handles credential verification during login and account creation during registration.
 * On success, both operations return a signed JWT together with basic user information.
 *
 * @author Nici0211
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authenticates a user by verifying their e-mail and password.
     * Throws an {@link ApiException} with status {@code 404} if no account is found
     * for the given e-mail, or {@code 401} if the password does not match.
     *
     * @param request the login credentials
     * @return an {@link AuthResponse} containing a signed JWT and the user's profile data
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Kein Konto mit dieser E-Mail gefunden.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException("E-Mail oder Passwort ist falsch.", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(toDTO(user))
                .build();
    }

    /**
     * Registers a new customer account with the role {@code CUSTOMER}.
     * If {@code vehicleBrand} and {@code vehicleModel} are both provided and non-blank
     * in the request, a {@link Vehicle} is created and linked to the new user.
     * Throws an {@link ApiException} with status {@code 409} if the e-mail is already taken.
     *
     * @param request the registration data including personal details and optional vehicle information
     * @return an {@link AuthResponse} containing a signed JWT and the newly created user's profile data
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Diese E-Mail ist bereits vergeben.", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .street(request.getStreet())
                .zip(request.getZip())
                .city(request.getCity())
                .role(Role.CUSTOMER)
                .createdAt(java.time.LocalDateTime.now())
                .vehicles(new ArrayList<>())
                .appointments(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();

        user = userRepository.save(user);

        // Optionally create a vehicle if brand and model are provided
        if (request.getVehicleBrand() != null && !request.getVehicleBrand().isBlank()
                && request.getVehicleModel() != null && !request.getVehicleModel().isBlank()) {

            Vehicle vehicle = Vehicle.builder()
                    .user(user)
                    .brand(request.getVehicleBrand())
                    .model(request.getVehicleModel())
                    .buildYear(request.getVehicleBuildYear())
                    .licensePlate(request.getVehicleLicensePlate())
                    .appointments(new ArrayList<>())
                    .build();

            vehicleRepository.save(vehicle);
        }

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(toDTO(user))
                .build();
    }

    /**
     * Maps a {@link User} entity to a {@link UserDTO} for use in API responses.
     *
     * @param user the entity to map
     * @return a lightweight DTO representation of the user
     */
    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .street(user.getStreet())
                .zip(user.getZip())
                .city(user.getCity())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}