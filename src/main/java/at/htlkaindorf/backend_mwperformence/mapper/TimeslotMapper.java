package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.TimeslotDTO;
import at.htlkaindorf.backend_mwperformence.entites.Timeslot;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:04
 */

@Mapper(componentModel = "spring")
public interface TimeslotMapper {
    TimeslotDTO toDto(Timeslot timeslot);
    Timeslot toEntity(TimeslotDTO timeslotDTO);
    List<TimeslotDTO> toDto(List<Timeslot> timeslots);
}