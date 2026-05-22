package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.OfferDTO;
import at.htlkaindorf.backend_mwperformence.entites.Offer;
import at.htlkaindorf.backend_mwperformence.mapper.OfferMapper;
import at.htlkaindorf.backend_mwperformence.repositories.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<OfferDTO> getAll() {
        return offerMapper.toDto(offerRepository.findAll());
    }

    public List<OfferDTO> getAllByActive(Boolean active) {
        return offerMapper.toDto(offerRepository.findByActive(active));
    }

    public OfferDTO getById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + id));
        return offerMapper.toDto(offer);
    }

    public OfferDTO create(OfferDTO dto) {
        Offer offer = offerMapper.toEntity(dto);
        return offerMapper.toDto(offerRepository.save(offer));
    }

    public OfferDTO update(Long id, OfferDTO dto) {
        offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found: " + id));
        Offer offer = offerMapper.toEntity(dto);
        offer.setId(id);
        return offerMapper.toDto(offerRepository.save(offer));
    }

    public void delete(Long id) {
        offerRepository.deleteById(id);
    }
}
