package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.request.EquipmentRq;
import com.fitnesscenter.app.dto.response.EquipmentRs;
import com.fitnesscenter.app.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentRs> get(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @GetMapping
    public ResponseEntity<List<EquipmentRs>> getAll() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @PostMapping
    public ResponseEntity<EquipmentRs> create(@RequestBody EquipmentRq request) {
        return ResponseEntity.ok(equipmentService.createEquipment(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EquipmentRs> changeStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(equipmentService.changeStatus(id, status));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<EquipmentRs>> getByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(equipmentService.getByZone(zoneId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EquipmentRs>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(equipmentService.getByStatus(status));
    }
}
