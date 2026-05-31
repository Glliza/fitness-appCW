package com.fitnesscenter.app.controller;

import com.fitnesscenter.app.dto.request.ConsumablesZonesRq;
import com.fitnesscenter.app.dto.response.ConsumablesRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.service.ConsumablesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consumables")
@RequiredArgsConstructor
public class ConsumablesController {
    private final ConsumablesService consumablesService;

    @GetMapping("/{id}")
    public ResponseEntity<ConsumablesRs> get(@PathVariable Long id) {
        return ResponseEntity.ok(consumablesService.getConsumablesById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ConsumablesRs>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(consumablesService.getAllConsumables(pageable));
    }

    @PostMapping("/income")
    public ResponseEntity<ConsumablesZonesRs> addIncome(@Valid @RequestBody ConsumablesZonesRq request) {
        return ResponseEntity.ok(consumablesService.addIncome(
                request.getConsumablesId(),
                request.getZoneId(),
                request.getCount()));
    }

    @PostMapping("/expense")
    public ResponseEntity<ConsumablesZonesRs> addExpense(@Valid @RequestBody ConsumablesZonesRq request) {
        return ResponseEntity.ok(consumablesService.addExpense(
                request.getConsumablesId(),
                request.getZoneId(),
                request.getCount()));
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(
            @RequestParam Long consumableId,
            @RequestParam Long zoneId) {
        return ResponseEntity.ok(consumablesService.getCurrentBalance(consumableId, zoneId));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam String format) {
        byte[] report = consumablesService.exportBalanceReport(format);

        String contentType;
        String extension;

        if ("pdf".equalsIgnoreCase(format)) {
            contentType = "application/pdf";
            extension = "pdf";
        } else {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            extension = "xlsx";
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=consumables_report." + extension)
                .body(report);
    }
}