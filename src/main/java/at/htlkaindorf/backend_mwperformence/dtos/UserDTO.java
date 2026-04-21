package at.htlkaindorf.backend_mwperformence.dtos;

import at.htlkaindorf.backend_mwperformence.entites.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 02.04.2026
 * Time: 23:03
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String street;
    private String zip;
    private String city;
    private Role role;
    private LocalDateTime createdAt;
}