package at.htlkaindorf.backend_mwperformence.controller;

import at.htlkaindorf.backend_mwperformence.dtos.ContactInfoDTO;
import at.htlkaindorf.backend_mwperformence.entites.ContactInfo;
import at.htlkaindorf.backend_mwperformence.mapper.ContactInfoMapper;
import at.htlkaindorf.backend_mwperformence.repositories.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 09:48
 */


@RestController
@RequestMapping("/api/contact-info")
@RequiredArgsConstructor
public class ContactInfoController {

    private final ContactInfoRepository contactInfoRepository;
    private final ContactInfoMapper contactInfoMapper;

    @GetMapping
    public List<ContactInfoDTO> getAll() {
        return contactInfoMapper.toDto(
                contactInfoRepository.findAllByOrderBySortAsc()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactInfoDTO> getById(@PathVariable Long id) {
        return contactInfoRepository.findById(id)
                .map(contactInfoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ContactInfoDTO create(@RequestBody ContactInfoDTO contactInfoDTO) {
        ContactInfo saved = contactInfoRepository.save(
                contactInfoMapper.toEntity(contactInfoDTO)
        );
        return contactInfoMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactInfoDTO> update(@PathVariable Long id,
                                                 @RequestBody ContactInfoDTO updated) {
        return contactInfoRepository.findById(id)
                .map(existing -> {
                    existing.setIcon(updated.getIcon());
                    existing.setValue(updated.getValue());
                    existing.setSort(updated.getSort());
                    return ResponseEntity.ok(
                            contactInfoMapper.toDto(contactInfoRepository.save(existing))
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!contactInfoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        contactInfoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
