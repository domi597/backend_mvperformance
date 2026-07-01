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
        return offerRepository.findAll().stream()
                .map(this::encodeIcon)
                .toList();
    }

    public List<OfferDTO> getAllByActive(Boolean active) {
        return offerRepository.findByActive(active).stream()
                .map(this::encodeIcon)
                .toList();
    }

    public OfferDTO getById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found: " + id));
        return encodeIcon(offer);
    }

    public OfferDTO create(OfferDTO dto) {
        Offer offer = offerMapper.toEntity(dto);
        offer.setCreatedAt(LocalDateTime.now());
        if (offer.getActive() == null) offer.setActive(true);
        offer.setServiceEntities(resolveServices(dto));
        if (dto.getIcon() != null)
            offer.setIcon(Base64.getDecoder().decode(dto.getIcon()));
        return encodeIcon(offerRepository.save(offer));
    }

    public OfferDTO update(Long id, OfferDTO dto) {
        Offer existing = offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found: " + id));

        if (dto.getTitle() != null)
            existing.setTitle(dto.getTitle());

        if (dto.getDescription() != null)
            existing.setDescription(dto.getDescription());

        if (dto.getPrice() != null)
            existing.setPrice(dto.getPrice());

        if (dto.getDuration() != null)
            existing.setDuration(dto.getDuration());

        if (dto.getActive() != null)
            existing.setActive(dto.getActive());

        if (dto.getServices() != null)
            existing.setServiceEntities(resolveServices(dto));

        if (dto.getIcon() != null)
            existing.setIcon(Base64.getDecoder().decode(dto.getIcon()));

        return encodeIcon(offerRepository.save(existing));
    }

    private OfferDTO encodeIcon(Offer offer) {
        OfferDTO dto = offerMapper.toDto(offer);
        if (offer.getIcon() != null)
            dto.setIcon(Base64.getEncoder().encodeToString(offer.getIcon()));
        return dto;
    }

    public void delete(Long id) {
        offerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offer not found: " + id));
        offerRepository.deleteById(id);
    }

    private List<ServiceEntity> resolveServices(OfferDTO dto) {
        if (dto.getServices() == null) return new java.util.ArrayList<>();
        return dto.getServices().stream()
                .map(s -> serviceRepository.findById(s.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found: " + s.getId())))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }
}