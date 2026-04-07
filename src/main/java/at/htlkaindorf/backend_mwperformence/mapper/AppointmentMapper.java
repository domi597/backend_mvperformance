package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 07.04.2026
 * Time: 10:21
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentDTO toDto(Appointment appointment);

    List<AppointmentDTO> toDto(List<Appointment> appointments);

    Appointment toEntity(AppointmentDTO appointmentDTO);

    List<Appointment> toEntity(List<AppointmentDTO> appointmentDTOs);
}
