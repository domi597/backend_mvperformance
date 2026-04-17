package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting between {@link Appointment} entities and
 * {@link AppointmentDTO} objects.
 * <p>
 * Spring manages the implementing class as a bean ({@code componentModel = "spring"}),
 * so it can be injected directly into services and controllers.
 * </p>
 *
 * @author Dominik Ranegger
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    /**
     * Converts a single {@link Appointment} entity to an {@link AppointmentDTO}.
     *
     * @param appointment the entity to convert
     * @return the corresponding DTO
     */
    AppointmentDTO toDto(Appointment appointment);

    /**
     * Converts a list of {@link Appointment} entities to a list of {@link AppointmentDTO}s.
     *
     * @param appointments the list of entities to convert
     * @return a list of corresponding DTOs
     */
    List<AppointmentDTO> toDto(List<Appointment> appointments);

    /**
     * Converts an {@link AppointmentDTO} back to an {@link Appointment} entity.
     *
     * @param appointmentDTO the DTO to convert
     * @return the corresponding entity
     */
    Appointment toEntity(AppointmentDTO appointmentDTO);

    /**
     * Converts a list of {@link AppointmentDTO}s to a list of {@link Appointment} entities.
     *
     * @param appointmentDTOs the list of DTOs to convert
     * @return a list of corresponding entities
     */
    List<Appointment> toEntity(List<AppointmentDTO> appointmentDTOs);
}
