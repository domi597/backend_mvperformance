package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 30.06.2026
 * Time: 21:48
 * Request body for an admin setting a new password for a user.
 * Never carries the old password — only the new one to be hashed.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen lang sein.")
    private String password;
}