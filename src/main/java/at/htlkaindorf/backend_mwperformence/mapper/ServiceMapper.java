package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.ServiceEntityDTO;
import at.htlkaindorf.backend_mwperformence.entites.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:53
 */
@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "icon", ignore = true)
    ServiceEntity toEntity(ServiceEntityDTO serviceDTO);

    @Mapping(target = "icon", ignore = true)
    ServiceEntityDTO toDto(ServiceEntity service);

    List<ServiceEntityDTO> toDto(List<ServiceEntity> service);

    List<ServiceEntity> toEntity(List<ServiceEntityDTO> serviceDTO);
}