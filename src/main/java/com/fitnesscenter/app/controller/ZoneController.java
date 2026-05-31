package com.fitnesscenter.app.controller;

import com.fitnesscenter.app.dto.request.ZoneRq;
import com.fitnesscenter.app.dto.response.ZoneRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {
    private final ZoneService zoneService;

    @GetMapping("/{id}")
    public ResponseEntity<ZoneRs> get(@PathVariable Long id) {
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    @PostMapping
    public ResponseEntity<ZoneRs> create(@Valid @RequestBody ZoneRq request) {
        return ResponseEntity.ok(zoneService.createZone(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneRs> update(@PathVariable Long id, @Valid @RequestBody ZoneRq request) {
        return ResponseEntity.ok(zoneService.updateZone(request, id));
    }

    // Новый метод с пагинацией
    @GetMapping
    public ResponseEntity<Page<ZoneRs>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(zoneService.getAllZones(pageable));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{zoneId}/consumables")
    public ResponseEntity<List<ConsumablesZonesRs>> getConsumables(@PathVariable Long zoneId) {
        return ResponseEntity.ok(zoneService.getConsumablesByZone(zoneId));
    }
}