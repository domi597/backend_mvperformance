package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.SelfChangePasswordRequest;
import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.entites.Role;
import at.htlkaindorf.backend_mwperformence.entites.User;
import at.htlkaindorf.backend_mwperformence.exception.ApiException;
import at.htlkaindorf.backend_mwperformence.mapper.UserMapper;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import at.htlkaindorf.backend_mwperformence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    /** Termin-Status, die als "offen" gelten und ein Löschen des Kunden verhindern. */
    private static final List<AppointmentStatus> OPEN_STATUSES = List.of(
            AppointmentStatus.NEU,
            AppointmentStatus.AUSSTEHEND,
            AppointmentStatus.BESTÄTIGT
    );

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;

    /**
     * Loads the currently authenticated user by the e-mail stored in their JWT.
     */
    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND));
    }

    /**
     * Allows the request only if the caller is an ADMIN or is acting on their own account.
     * Used for routes shared between the admin customer-management screen and the
     * customer's own account page (view/update/delete profile).
     */
    private void assertSelfOrAdmin(Long targetId, Authentication authentication) {
        User requester = currentUser(authentication);
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isAdmin && !requester.getId().equals(targetId)) {
            throw new ApiException("Zugriff verweigert: Du kannst nur dein eigenes Konto bearbeiten.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Allows the request only if the caller is acting on their own account — even an
     * ADMIN may not use this to change someone else's password.
     */
    private void assertSelf(Long targetId, Authentication authentication) {
        User requester = currentUser(authentication);
        if (!requester.getId().equals(targetId)) {
            throw new ApiException("Du kannst nur dein eigenes Passwort ändern.", HttpStatus.FORBIDDEN);
        }
    }

    public UserDTO getById(Long id, Authentication authentication) {
        assertSelfOrAdmin(id, authentication);
        return userMapper.toDTO(userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND)));
    }

    public List<UserDTO> getAll() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto, Authentication authentication) {
        assertSelfOrAdmin(id, authentication);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getStreet() != null) user.setStreet(dto.getStreet());
        if (dto.getZip() != null) user.setZip(dto.getZip());
        if (dto.getCity() != null) user.setCity(dto.getCity());

        return userMapper.toDTO(userRepository.save(user));
    }

    /**
     * Sets a new password for a user (admin-triggered reset).
     * The old password is never read or exposed — only the new value is hashed and stored.
     */
    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Self-service password change from the account page.
     * Verifies the current password before applying the new one, and requires the
     * new password to be entered identically twice to catch typos.
     */
    @Transactional
    public void changeOwnPassword(Long id, SelfChangePasswordRequest request, Authentication authentication) {
        assertSelf(id, authentication);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new ApiException("Das aktuelle Passwort ist falsch.", HttpStatus.BAD_REQUEST);
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new ApiException("Die neuen Passwörter stimmen nicht überein.", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new ApiException("Das neue Passwort muss sich vom aktuellen unterscheiden.", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Deletes a user account, but only if no open appointments (NEU, AUSSTEHEND, BESTÄTIGT) exist.
     * Closed appointments (ABGESCHLOSSEN, ABGELEHNT) are kept for the yearly overview —
     * they are detached from the account (user = null) instead of being deleted, since
     * Appointment.customerName already stores the name independently of the User relation.
     */
    @Transactional
    public void delete(Long id, Authentication authentication) {
        assertSelfOrAdmin(id, authentication);
        if (!userRepository.existsById(id)) {
            throw new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND);
        }

        long openCount = appointmentRepository.countByUserIdAndStatusIn(id, OPEN_STATUSES);
        if (openCount > 0) {
            throw new ApiException(
                    "Kunde kann nicht gelöscht werden: Es bestehen noch " + openCount +
                            " offene(r) Termin(e). Bitte zuerst abschließen oder ablehnen.",
                    HttpStatus.CONFLICT
            );
        }

        List<Appointment> appointments = appointmentRepository.findByUserId(id);
        appointments.forEach(a -> a.setUser(null));
        appointmentRepository.saveAll(appointments);
        appointmentRepository.flush();

        userRepository.deleteById(id);
    }
}