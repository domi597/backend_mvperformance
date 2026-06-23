package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.OfferDTO;
import at.htlkaindorf.backend_mwperformence.entites.Offer;
import at.htlkaindorf.backend_mwperformence.entites.ServiceEntity;
import at.htlkaindorf.backend_mwperformence.mapper.OfferMapper;
import at.htlkaindorf.backend_mwperformence.repositories.OfferRepository;
import at.htlkaindorf.backend_mwperformence.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 22.05.2026
 * Time: 11:08
 */

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;
    private final ServiceRepository serviceRepository;

    public List<OfferDTO> getAll() {
        return offerMapper.toDto(offerRepository.findAll());
    }

    public List<OfferDTO> getAllByActive(Boolean active) {
        return offerMapper.toDto(offerRepository.findByActive(active));
    }

    public OfferDTO getById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found: " + id));
        return offerMapper.toDto(offer);
    }

    public OfferDTO create(OfferDTO dto) {
        Offer offer = offerMapper.toEntity(dto);
        offer.setCreatedAt(LocalDateTime.now());
        if (offer.getActive() == null) offer.setActive(true);
        offer.setServiceEntities(resolveServices(dto));
        return offerMapper.toDto(offerRepository.save(offer));
    }

    public OfferDTO update(Long id, OfferDTO dto) {
        Offer existing = offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found: " + id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setDuration(dto.getDuration());
        existing.setActive(dto.getActive() != null ? dto.getActive() : true);
        existing.setServiceEntities(resolveServices(dto));

        return offerMapper.toDto(offerRepository.save(existing));
    }

    public void delete(Long id) {
        offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found: " + id));
        offerRepository.deleteById(id);
    }

    private List<ServiceEntity> resolveServices(OfferDTO dto) {
        if (dto.getServices() == null) return List.of();
        return dto.getServices().stream()
                .map(s -> serviceRepository.findById(s.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found: " + s.getId())))
                .toList();
    }
}
