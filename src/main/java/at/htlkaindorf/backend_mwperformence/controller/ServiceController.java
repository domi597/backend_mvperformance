package at.htlkaindorf.backend_mwperformence.controller;


import at.htlkaindorf.backend_mwperformence.dtos.ServiceDTO;
import at.htlkaindorf.backend_mwperformence.entites.Service;
import at.htlkaindorf.backend_mwperformence.mapper.ServiceMapper;
import at.htlkaindorf.backend_mwperformence.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 27.03.2026
 * Time: 09:47
 */

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @GetMapping
    public List<ServiceDTO> getAll() {
        return serviceMapper.toDto(
                serviceRepository.findAllByOrderBySortAsc()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(serviceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<ServiceDTO> searchByTitle(@RequestParam String title) {
        return serviceMapper.toDto(
                serviceRepository.findAllByTitle("%" + title + "%")
        );
    }

    @PostMapping
    public ServiceDTO create(@RequestBody ServiceDTO serviceDTO) {
        Service saved = serviceRepository.save(
                serviceMapper.toEntity(serviceDTO)
        );
        return serviceMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> update(@PathVariable Long id,
                                             @RequestBody ServiceDTO updated) {
        return serviceRepository.findById(id)
                .map(existing -> {
                    existing.setIcon(updated.getIcon());
                    existing.setTitle(updated.getTitle());
                    existing.setSubtitle(updated.getSubtitle());
                    existing.setSort(updated.getSort());
                    return ResponseEntity.ok(
                            serviceMapper.toDto(serviceRepository.save(existing))
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!serviceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        serviceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
