package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 04.07.2026
 * Time: 22:03
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "E-Mail ist erforderlich.")
    @Email(message = "Bitte eine gültige E-Mail-Adresse eingeben.")
    private String email;
}
