package at.htlkaindorf.backend_mwperformence.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:37
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String icon;

    @Column(nullable = false)
    private String value;
}
