package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.ServiceDTO;
import at.htlkaindorf.backend_mwperformence.entites.Service;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:53
 */
@Mapper(componentModel = "spring")
public interface ServiceMapper {

    Service toEntity(ServiceDTO serviceDTO);

    List<Service> toEntity(List<ServiceDTO> serviceDTO);

    ServiceDTO toDto(Service service);

    List<ServiceDTO> toDto(List<Service> service);
}
