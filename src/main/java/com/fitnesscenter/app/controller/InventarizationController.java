package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.response.InventarizationRs;
import com.fitnesscenter.app.dto.response.InventarizationReportRs;
import com.fitnesscenter.app.dto.response.InventarizationAllRs;
import com.fitnesscenter.app.service.InventarizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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
        return ResponseEntity.ok(inventarizationService.startInventarization());
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
        return ResponseEntity.ok(inventarizationService.finishInventarization());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam(required = false) Long zoneId,
            @RequestParam String format) {

        byte[] report = inventarizationService.exportInventarizationReport(zoneId, format);

        String contentType = "application/pdf".equals(format) ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String extension = "pdf".equals(format) ? "pdf" : "xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventarization_report." + extension)
                .body(report);
    }
}
