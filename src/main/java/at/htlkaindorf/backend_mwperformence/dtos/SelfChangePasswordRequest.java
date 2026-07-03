package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 03.07.2026
 * Time: 20:48
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelfChangePasswordRequest {

    @NotBlank(message = "Aktuelles Passwort ist erforderlich.")
    private String oldPassword;

    @NotBlank
    @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen lang sein.")
    private String newPassword;

    @NotBlank(message = "Bitte das neue Passwort bestätigen.")
    private String newPasswordConfirm;
}