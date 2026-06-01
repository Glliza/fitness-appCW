package com.fitnesscenter.app.controller;

import com.fitnesscenter.app.dto.response.InventarizationRs;
import com.fitnesscenter.app.dto.response.InventarizationReportRs;
import com.fitnesscenter.app.dto.response.InventarizationAllRs;
import com.fitnesscenter.app.service.InventarizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventarization")
@RequiredArgsConstructor
public class InventarizationController {
    private final InventarizationService inventarizationService;

    @PostMapping("/start")
    public ResponseEntity<List<InventarizationRs>> start(@RequestParam Long zoneId) {
        return ResponseEntity.ok(inventarizationService.startInventarization(zoneId));
    }

    @PostMapping("/start-all")
    public ResponseEntity<List<InventarizationAllRs>> startAll() {
        return ResponseEntity.ok(inventarizationService.startInventarizationAll());
    }

    @PostMapping("/step")
    public ResponseEntity<InventarizationRs> performStep(
            @RequestParam Long invId,
            @RequestParam Integer actualCount) {
        return ResponseEntity.ok(inventarizationService.performStep(invId, actualCount));
    }

    @PostMapping("/finish")
    public ResponseEntity<InventarizationReportRs> finish(@RequestParam Long zoneId) {
        return ResponseEntity.ok(inventarizationService.finishInventarization(zoneId));
    }

    @PostMapping("/finish-all")
    public ResponseEntity<InventarizationReportRs> finishAll() {
        return ResponseEntity.ok(inventarizationService.finishInventarizationAll());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<InventarizationReportRs>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return ResponseEntity.ok(inventarizationService.getAllInventarizationHistory(pageable));
    }
}