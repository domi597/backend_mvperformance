package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.ServiceEntityDTO;
import at.htlkaindorf.backend_mwperformence.entites.Offer;
import at.htlkaindorf.backend_mwperformence.entites.ServiceEntity;
import at.htlkaindorf.backend_mwperformence.mapper.ServiceMapper;
import at.htlkaindorf.backend_mwperformence.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 21.04.2026
 * Time: 11:08
 */

@Service
@RequiredArgsConstructor
public class ServiceEntityService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public List<ServiceEntityDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::encodeIcon)
                .toList();
    }

    public ServiceEntityDTO getServiceById(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service nicht gefunden: " + id));
        return encodeIcon(service);
    }

    public ServiceEntityDTO createService(ServiceEntityDTO dto) {
        ServiceEntity entity = serviceMapper.toEntity(dto);
        if (dto.getIcon() != null)
            entity.setIcon(Base64.getDecoder().decode(dto.getIcon()));
        return encodeIcon(serviceRepository.save(entity));
    }

    public ServiceEntityDTO updateService(Long id, ServiceEntityDTO dto) {
        ServiceEntity entity = serviceRepository.findById(id)
                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service nicht gefunden: " + id));

        if (dto.getIcon() != null)
            entity.setIcon(Base64.getDecoder().decode(dto.getIcon()));

        if (dto.getTitle() != null)
            entity.setTitle(dto.getTitle());

        if (dto.getSubtitle() != null)
            entity.setSubtitle(dto.getSubtitle());

        return encodeIcon(serviceRepository.save(entity));
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service nicht gefunden"));
        for (Offer offer : service.getOffers())
            offer.getServiceEntities().remove(service);
        service.getOffers().clear();
        serviceRepository.delete(service);
    }

    private ServiceEntityDTO encodeIcon(ServiceEntity entity) {
        ServiceEntityDTO dto = serviceMapper.toDto(entity);
        if (entity.getIcon() != null)
            dto.setIcon(Base64.getEncoder().encodeToString(entity.getIcon()));
        return dto;
    }
}