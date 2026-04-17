package at.htlkaindorf.backend_mwperformence.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * JPA entity representing a service offered by the workshop (e.g. "Oil Change", "Tire Fitting").
 * <p>
 * Services are displayed on the frontend with an icon, title and subtitle. The {@code sort}
 * field controls the display order. A service can be linked to many {@link Appointment}s and
 * can appear in multiple {@link Offer}s through a many-to-many relationship.
 * </p>
 *
 * @author Dominik Ranegger
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String icon;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String subtitle;

    @Column(nullable = false)
    private Integer sort;

    @OneToMany(mappedBy = "service", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Appointment> appointments;

    @ManyToMany(mappedBy = "services")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Offer> offers;
}
