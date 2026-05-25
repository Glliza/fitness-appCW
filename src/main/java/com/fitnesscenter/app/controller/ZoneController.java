package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.request.ZoneRq;
import com.fitnesscenter.app.dto.response.ZoneRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.service.ZoneService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<ZoneRs>> getAll() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @PostMapping
    public ResponseEntity<ZoneRs> create(@RequestBody ZoneRq request) {
        return ResponseEntity.ok(zoneService.createZone(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneRs> update(@PathVariable Long id, @RequestBody ZoneRq request) {
        return ResponseEntity.ok(zoneService.updateZone(request, id));
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
