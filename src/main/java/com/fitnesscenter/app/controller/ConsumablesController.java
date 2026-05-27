package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.response.ConsumablesRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.service.ConsumablesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<List<ConsumablesRs>> getAll() {
        return ResponseEntity.ok(consumablesService.getAllConsumables());
    }

    @PostMapping("/income")
    public ResponseEntity<ConsumablesZonesRs> addIncome(
            @RequestParam Long consumableId,
            @RequestParam Long zoneId,
            @RequestParam Integer amount) {
        return ResponseEntity.ok(consumablesService.addIncome(consumableId, zoneId, amount));
    }

    @PostMapping("/expense")
    public ResponseEntity<ConsumablesZonesRs> addExpense(
            @RequestParam Long consumableId,
            @RequestParam Long zoneId,
            @RequestParam Integer amount) {
        return ResponseEntity.ok(consumablesService.addExpense(consumableId, zoneId, amount));
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
            // Правильный MIME тип для Excel
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
