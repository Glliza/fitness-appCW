package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.RequestBuyRq;
import com.fitnesscenter.app.dto.request.UpdateRequestBuyStatusRq;
import com.fitnesscenter.app.dto.response.RequestBuyRs;
import com.fitnesscenter.app.entity.RequestBuy;
import com.fitnesscenter.app.repository.RequestBuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestBuyService {
    private final RequestBuyRepository requestBuyRepository;

    @Transactional
    public RequestBuyRs createRequest(RequestBuyRq request) {
        RequestBuy buy = new RequestBuy();
        buy.setEquipmentInventoryNumber(request.getEquipmentInventoryNumber());
        buy.setName(request.getName());
        buy.setCount(request.getCount());
        buy.setStatus("В рассмотрении");  // Устанавливаем начальный статус

        RequestBuy saved = requestBuyRepository.save(buy);
        return mapToRs(saved);
    }

    public List<RequestBuyRs> getAllRequests() {
        return requestBuyRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestBuyRs updateStatus(Long id, String status) {
        RequestBuy buy = requestBuyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        buy.setStatus(status);
        return mapToRs(requestBuyRepository.save(buy));
    }

    private RequestBuyRs mapToRs(RequestBuy entity) {
        return RequestBuyRs.builder()
                .id(entity.getId())
                .equipmentInventoryNumber(entity.getEquipmentInventoryNumber())
                .count(entity.getCount())
                .name(entity.getName())
                .created_at(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDate() : LocalDate.now())
                .status(entity.getStatus() != null ? entity.getStatus() : "В рассмотрении")
                .build();
    }
}