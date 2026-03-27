package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 11:12
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "preferredDate", dateFormat = "dd.MM.yyyy HH:mm")
    AppointmentDTO toDto(Appointment entity);

    @Mapping(target = "preferredDate", dateFormat = "dd.MM.yyyy HH:mm")
    Appointment toEntity(AppointmentDTO dto);
}
