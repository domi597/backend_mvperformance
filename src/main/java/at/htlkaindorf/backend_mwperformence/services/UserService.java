package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import at.htlkaindorf.backend_mwperformence.entites.AppointmentStatus;
import at.htlkaindorf.backend_mwperformence.entites.User;
import at.htlkaindorf.backend_mwperformence.exception.ApiException;
import at.htlkaindorf.backend_mwperformence.mapper.UserMapper;
import at.htlkaindorf.backend_mwperformence.repositories.AppointmentRepository;
import at.htlkaindorf.backend_mwperformence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public UserDTO getById(Long id) {
        return userMapper.toDTO(userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND)));
    }

    public List<UserDTO> getAll() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
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
     * Deletes a user account, but only if no open appointments (NEU, AUSSTEHEND, BESTÄTIGT) exist.
     * Closed appointments (ABGESCHLOSSEN, ABGELEHNT) are kept for the yearly overview —
     * they are detached from the account (user = null) instead of being deleted, since
     * Appointment.customerName already stores the name independently of the User relation.
     */
    @Transactional
    public void delete(Long id) {
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