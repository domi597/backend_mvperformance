package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.AppointmentDTO;
import at.htlkaindorf.backend_mwperformence.entites.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 07.04.2026
 * Time: 10:21
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "customerId",   source = "user.id")
    @Mapping(target = "vehicleId",    source = "vehicleEntity.id")
    @Mapping(target = "serviceId",    source = "serviceEntity.id")
    @Mapping(target = "serviceType",  source = "serviceType")
    @Mapping(target = "brand",        source = "vehicleEntity.brand")
    @Mapping(target = "model",        source = "vehicleEntity.model")
    @Mapping(target = "year",         source = "vehicleEntity.buildYear")
    @Mapping(target = "licensePlate", source = "vehicleEntity.licensePlate")
    @Mapping(target = "date",      expression = "java(appointment.getPreferredDate().toLocalDate().toString())")
    @Mapping(target = "time",      expression = "java(appointment.getPreferredDate().toLocalTime().toString().substring(0,5))")
    @Mapping(target = "createdAt", expression = "java(appointment.getCreatedAt() != null ? appointment.getCreatedAt().toString() : null)")
    AppointmentDTO toDto(Appointment appointment);

    @Mapping(target = "user",          ignore = true)
    @Mapping(target = "vehicleEntity", ignore = true)
    @Mapping(target = "serviceEntity", ignore = true)
    @Mapping(target = "status",        ignore = true)
    @Mapping(target = "createdAt",     ignore = true)
    @Mapping(target = "vehicle",       ignore = true)
    // serviceType und price werden NICHT ignoriert → kommen direkt aus dem DTO
    @Mapping(target = "preferredDate", expression = "java(mapDateTime(dto.getDate(), dto.getTime()))")
    Appointment toEntity(AppointmentDTO dto);

    List<AppointmentDTO> toDto(List<Appointment> appointments);

    default LocalDateTime mapDateTime(String date, String time) {
        if (date == null || time == null) return null;
        try {
            return LocalDateTime.parse(date + "T" + (time.length() == 5 ? time : "0" + time));
        } catch (Exception e) {
            return null;
        }
    }
}
