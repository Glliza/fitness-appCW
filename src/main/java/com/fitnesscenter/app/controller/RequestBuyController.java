package com.fitnesscenter.app.controller;


import com.fitnesscenter.app.dto.request.RequestBuyRq;
import com.fitnesscenter.app.dto.response.RequestBuyRs;
import com.fitnesscenter.app.service.RequestBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
public class RequestBuyController {
    private final RequestBuyService requestBuyService;

    @PostMapping
    public ResponseEntity<RequestBuyRs> create(@RequestBody RequestBuyRq request) {
        return ResponseEntity.ok(requestBuyService.createRequest(request));
    }

    @GetMapping
    public ResponseEntity<List<RequestBuyRs>> getAll() {
        return ResponseEntity.ok(requestBuyService.getAllRequests());
    }
}
