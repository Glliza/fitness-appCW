package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.request.TORepairRq;
import com.fitnesscenter.app.dto.request.RequestRepairRq;
import com.fitnesscenter.app.dto.response.TORepairRs;
import com.fitnesscenter.app.dto.response.RequestRepairRs;
import com.fitnesscenter.app.dto.response.EquipmentHistoryRs;
import com.fitnesscenter.app.service.NotificationService;
import com.fitnesscenter.app.service.TORepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import com.fitnesscenter.app.entity.Notification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class TORepairController {
    private final TORepairService toRepairService;
    private final NotificationService notificationService;

    @GetMapping("/unread/{adminId}")
    public ResponseEntity<List<Notification>> getUnread(@PathVariable Long adminId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(adminId));
    }

    @PostMapping("/to")
    public ResponseEntity<TORepairRs> createTO(@RequestBody TORepairRq request) {
        return ResponseEntity.ok(toRepairService.createTO(request));
    }

    @PostMapping("/repairs")
    public ResponseEntity<RequestRepairRs> registerRepair(@RequestBody RequestRepairRq request) {
        return ResponseEntity.ok(toRepairService.registerRepairRequest(request));
    }

    @PatchMapping("/repairs/{requestId}/status")
    public ResponseEntity<RequestRepairRs> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam String worker) {
        return ResponseEntity.ok(toRepairService.updateRequestStatus(requestId, status, worker));
    }

    @GetMapping("/repairs")
    public ResponseEntity<List<RequestRepairRs>> getRequests(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            @RequestParam(required = false) Long equipmentId) {
        return ResponseEntity.ok(toRepairService.getRequestsByFilters(type, start, end, equipmentId));
    }

    @GetMapping("/history/{equipmentId}")
    public ResponseEntity<List<EquipmentHistoryRs>> getHistory(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(toRepairService.getMaintenanceHistory(equipmentId));
    }

    @GetMapping("/next-date/{equipmentId}")
    public ResponseEntity<LocalDate> nextTODate(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(toRepairService.calculateNextTODate(equipmentId));
    }

}
