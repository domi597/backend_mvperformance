package at.htlkaindorf.backend_mwperformence.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for the {@code POST /api/auth/register} endpoint.
 * <p>
 * Mandatory personal data (first name, last name, e-mail, password) must pass
 * Bean Validation before the request is processed. Vehicle fields are optional;
 * a vehicle is only persisted when both {@code vehicleBrand} and
 * {@code vehicleModel} are provided and non-blank.
 * </p>
 */
@Data
public class RegisterRequest {

    /** The user's first name. Must not be blank. */
    @NotBlank
    private String firstName;

    /** The user's last name. Must not be blank. */
    @NotBlank
    private String lastName;

    /** The user's e-mail address. Must be well-formed and not blank. */
    @NotBlank
    @Email
    private String email;

    /** The desired plain-text password. Must be at least 6 characters long. */
    @NotBlank
    @Size(min = 6)
    private String password;

    /** Optional phone number of the user. */
    private String phone;
    /** Optional street address of the user. */
    private String street;
    /** Optional city of the user. */
    private String city;

    // Optional vehicle data — only stored when brand AND model are provided
    /** Brand of the vehicle to register together with the account (optional). */
    private String vehicleBrand;
    /** Model of the vehicle to register together with the account (optional). */
    private String vehicleModel;
    /** Build year of the vehicle (optional). */
    private Integer vehicleBuildYear;
    /** License plate of the vehicle (optional). */
    private String vehicleLicensePlate;
}
