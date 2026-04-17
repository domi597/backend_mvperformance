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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Login: E-Mail und Passwort prüfen, JWT zurückgeben.
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
     * Registrierung: neuen User anlegen, optional ein Fahrzeug mitanlegen.
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
                .city(request.getCity())
                .role(Role.CUSTOMER)
                .vehicles(new ArrayList<>())
                .appointments(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();

        user = userRepository.save(user);

        // Optionales Fahrzeug anlegen, wenn Marke und Modell angegeben
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

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .street(user.getStreet())
                .city(user.getCity())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
