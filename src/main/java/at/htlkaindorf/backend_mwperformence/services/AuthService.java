package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.config.JwtService;
import at.htlkaindorf.backend_mwperformence.dtos.AuthResponse;
import at.htlkaindorf.backend_mwperformence.dtos.ForgotPasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.LoginRequest;
import at.htlkaindorf.backend_mwperformence.dtos.PendingVerificationResponse;
import at.htlkaindorf.backend_mwperformence.dtos.RegisterRequest;
import at.htlkaindorf.backend_mwperformence.dtos.ResendVerificationRequest;
import at.htlkaindorf.backend_mwperformence.dtos.ResetPasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.dtos.VerifyEmailRequest;
import at.htlkaindorf.backend_mwperformence.entites.Role;
import at.htlkaindorf.backend_mwperformence.entites.User;
import at.htlkaindorf.backend_mwperformence.entites.Vehicle;
import at.htlkaindorf.backend_mwperformence.exception.ApiException;
import at.htlkaindorf.backend_mwperformence.mapper.UserMapper;
import at.htlkaindorf.backend_mwperformence.repositories.UserRepository;
import at.htlkaindorf.backend_mwperformence.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Service responsible for user authentication and registration.
 * Handles credential verification during login, account creation during registration,
 * and the "Passwort vergessen" flow (reset-token generation, validation and consumption).
 * On success, login/register return a signed JWT together with basic user information.
 *
 * @author Nici0211
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /** Wie lange ein Passwort-Reset-Link gültig ist, bevor er abläuft. */
    private static final int RESET_TOKEN_VALID_MINUTES = 60;

    /** Wie lange der E-Mail-Bestätigungscode gültig ist, bevor er abläuft. */
    private static final int VERIFICATION_CODE_VALID_MINUTES = 15;

    private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    /** Basis-URL des Frontends, um daraus den anklickbaren Reset-Link zu bauen. */
    @Value("${app.frontend.url}")
    private String frontendUrl;

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

        if (!user.isEmailVerified()) {
            throw new ApiException("Bitte bestätige zuerst deine E-Mail-Adresse.", HttpStatus.FORBIDDEN);
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
    public PendingVerificationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Diese E-Mail ist bereits vergeben.", HttpStatus.CONFLICT);
        }

        String verificationCode = generateVerificationCode();

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
                .createdAt(LocalDateTime.now())
                .emailVerified(false)
                .verificationCode(verificationCode)
                .verificationCodeExpiry(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALID_MINUTES))
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

        mailService.sendVerificationEmail(user, verificationCode);

        return PendingVerificationResponse.builder()
                .email(user.getEmail())
                .message("Wir haben dir einen Bestätigungscode an deine E-Mail-Adresse geschickt.")
                .build();
    }

    /**
     * Bestätigt die E-Mail-Adresse eines frisch registrierten Kontos anhand des 6-stelligen Codes,
     * der beim Registrieren per Mail verschickt wurde. Bei Erfolg wird das Konto als verifiziert
     * markiert und – wie bei Login/Register sonst auch – ein signiertes JWT ausgestellt.
     * Wirft eine {@link ApiException} ({@code 400}), wenn der Code falsch oder abgelaufen ist.
     *
     * @param request E-Mail-Adresse samt eingegebenem 6-stelligen Code
     * @return ein {@link AuthResponse} mit JWT und Profildaten des nun verifizierten Nutzers
     */
    @Transactional
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Kein Konto mit dieser E-Mail gefunden.", HttpStatus.NOT_FOUND));

        if (user.isEmailVerified()) {
            String token = jwtService.generateToken(user.getEmail());
            return AuthResponse.builder().token(token).user(toDTO(user)).build();
        }

        if (user.getVerificationCode() == null
                || user.getVerificationCodeExpiry() == null
                || user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new ApiException("Der Code ist abgelaufen. Bitte fordere einen neuen an.", HttpStatus.BAD_REQUEST);
        }

        if (!user.getVerificationCode().equals(request.getCode())) {
            throw new ApiException("Der eingegebene Code ist ungültig.", HttpStatus.BAD_REQUEST);
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(toDTO(user))
                .build();
    }

    /**
     * Verschickt einen neuen Bestätigungscode, falls das Konto noch nicht verifiziert ist.
     * Antwortet auch dann ohne Fehler, wenn keine E-Mail existiert, um nicht zu verraten,
     * welche Adressen registriert sind.
     *
     * @param request die E-Mail-Adresse, für die ein neuer Code verschickt werden soll
     */
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (user.isEmailVerified()) {
                return;
            }

            String code = generateVerificationCode();
            user.setVerificationCode(code);
            user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALID_MINUTES));
            userRepository.save(user);

            mailService.sendVerificationEmail(user, code);
        });
    }

    /** Erzeugt einen zufälligen 6-stelligen numerischen Bestätigungscode (führende Nullen möglich). */
    private String generateVerificationCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    /**
     * Startet den "Passwort vergessen"-Vorgang für die angegebene E-Mail-Adresse.
     * Antwortet dem Aufrufer bewusst immer gleich (kein Fehler, kein 404), egal ob die
     * E-Mail existiert oder nicht – so lässt sich nicht erraten, welche Adressen registriert sind.
     * Existiert ein Konto, wird ein Reset-Token erzeugt und ein Link per Mail versendet.
     *
     * @param request die E-Mail-Adresse, an die der Reset-Link geschickt werden soll
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(RESET_TOKEN_VALID_MINUTES));
            userRepository.save(user);

            String resetLink = frontendUrl + "/passwort-zuruecksetzen?token=" + token;
            mailService.sendPasswordResetEmail(user, resetLink);
        });
    }

    /**
     * Schließt den "Passwort vergessen"-Vorgang ab: prüft den Token und setzt das neue Passwort.
     * Wirft eine {@link ApiException} ({@code 400}), wenn der Token unbekannt, bereits verwendet
     * oder abgelaufen ist, oder wenn die beiden eingegebenen Passwörter nicht übereinstimmen.
     *
     * @param request Reset-Token aus dem E-Mail-Link samt neuem Passwort (zweifach eingegeben)
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new ApiException("Ungültiger oder abgelaufener Link.", HttpStatus.BAD_REQUEST));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ApiException("Ungültiger oder abgelaufener Link.", HttpStatus.BAD_REQUEST);
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new ApiException("Die neuen Passwörter stimmen nicht überein.", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
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

    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND));
        return userMapper.toDTO(user);
    }
}