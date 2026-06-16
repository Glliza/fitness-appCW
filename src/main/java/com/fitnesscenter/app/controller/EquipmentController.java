    package com.fitnesscenter.app.controller;

    import com.fitnesscenter.app.dto.request.EquipmentRq;
    import com.fitnesscenter.app.dto.response.EquipmentRs;
    import com.fitnesscenter.app.service.EquipmentService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import jakarta.validation.Valid;

    @RestController
    @RequestMapping("/api/equipment")
    @RequiredArgsConstructor
    public class EquipmentController {
        private final EquipmentService equipmentService;

        @GetMapping("/{id}")
        public ResponseEntity<EquipmentRs> get(@PathVariable Long id) {
            return ResponseEntity.ok(equipmentService.getEquipmentById(id));
        }

        @PostMapping
        public ResponseEntity<EquipmentRs> create(@Valid @RequestBody EquipmentRq request) {
            return ResponseEntity.ok(equipmentService.createEquipment(request));
        }

        @PutMapping("/{id}")
        public ResponseEntity<EquipmentRs> update(@PathVariable Long id, @Valid @RequestBody EquipmentRq request) {
            return ResponseEntity.ok(equipmentService.updateEquipment(id, request));
        }

        // Новый метод с пагинацией и фильтрацией
        @GetMapping
        public ResponseEntity<Page<EquipmentRs>> getAll(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "id") String sortBy,
                @RequestParam(defaultValue = "asc") String sortDir,
                @RequestParam(required = false) Long zoneId,
                @RequestParam(required = false) String status) {

            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<EquipmentRs> result;

            if (zoneId != null && status != null && !status.isEmpty()) {
                result = equipmentService.getEquipmentByZoneAndStatus(zoneId, status, pageable);
            } else if (zoneId != null) {
                result = equipmentService.getEquipmentByZone(zoneId, pageable);
            } else if (status != null && !status.isEmpty()) {
                result = equipmentService.getEquipmentByStatus(status, pageable);
            } else {
                result = equipmentService.getAllEquipment(pageable);
            }

            return ResponseEntity.ok(result);
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
    }