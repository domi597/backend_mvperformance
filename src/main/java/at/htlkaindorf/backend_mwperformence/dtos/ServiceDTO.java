package at.htlkaindorf.backend_mwperformence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a service offered by the workshop.
 * <p>
 * Used to expose service data through the REST API without leaking entity
 * relationships (e.g. the list of associated appointments or offers).
 * </p>
 *
 * @author Dominik Ranegger
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDTO {
    /** Unique identifier of the service. */
    private Long id;
    /** Emoji or icon identifier displayed alongside the service title. */
    private String icon;
    /** Human-readable name of the service (e.g. "Oil Change"). */
    private String title;
    /** Short description or subtitle of the service. */
    private String subtitle;
    /** Display order used for sorting services in the UI. */
    private Integer sort;
}
