package com.fitnesscenter.app.controller;

import com.fitnesscenter.app.dto.request.RequestBuyRq;
import com.fitnesscenter.app.dto.request.UpdateRequestBuyStatusRq;
import com.fitnesscenter.app.dto.response.RequestBuyRs;
import com.fitnesscenter.app.service.RequestBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class RequestBuyController {
    private final RequestBuyService requestBuyService;

    @GetMapping
    public ResponseEntity<Page<RequestBuyRs>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(requestBuyService.getAllRequests(pageable));
    }

    @PostMapping
    public ResponseEntity<RequestBuyRs> create(@RequestBody RequestBuyRq request) {
        return ResponseEntity.ok(requestBuyService.createRequest(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RequestBuyRs> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateRequestBuyStatusRq request) {
        return ResponseEntity.ok(requestBuyService.updateStatus(id, request.getStatus()));
    }
}