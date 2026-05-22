package at.htlkaindorf.backend_mwperformence.controller;


import at.htlkaindorf.backend_mwperformence.dtos.ServiceEntityDTO;
import at.htlkaindorf.backend_mwperformence.services.ServiceEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
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
public class ServiceEntityController {

    private final ServiceEntityService serviceEntityService;

    @GetMapping
    public ResponseEntity<List<ServiceEntityDTO>> getAll() {
        return ResponseEntity.ok(serviceEntityService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntityDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceEntityService.getServiceById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceEntityDTO> create(@RequestBody ServiceEntityDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceEntityService.createService(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceEntityDTO> update(@PathVariable Long id, @RequestBody ServiceEntityDTO dto) {
        return ResponseEntity.ok(serviceEntityService.updateService(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceEntityService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}