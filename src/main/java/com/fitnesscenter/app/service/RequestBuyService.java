package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.RequestBuyRq;
import com.fitnesscenter.app.dto.response.RequestBuyRs;
import com.fitnesscenter.app.entity.Administrator;
import com.fitnesscenter.app.entity.RequestBuy;
import com.fitnesscenter.app.repository.AdministratorRepository;
import com.fitnesscenter.app.repository.RequestBuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestBuyService {
    private final RequestBuyRepository requestBuyRepository;
    private final AdministratorRepository administratorRepository;
    private final NotificationService notificationService;

    // Новый метод с пагинацией
    public Page<RequestBuyRs> getAllRequests(Pageable pageable) {
        return requestBuyRepository.findAll(pageable)
                .map(this::mapToRs);
    }

    // Старый метод для совместимости (если нужен)
    public List<RequestBuyRs> getAllRequestsList() {
        return requestBuyRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestBuyRs createRequest(RequestBuyRq request) {
        RequestBuy buy = new RequestBuy();
        buy.setEquipmentInventoryNumber(request.getEquipmentInventoryNumber());
        buy.setName(request.getName());
        buy.setCount(request.getCount());
        buy.setStatus("В рассмотрении");

        RequestBuy saved = requestBuyRepository.save(buy);

        String message = String.format("Создана новая заявка на закупку: %s в количестве %d шт.",
                request.getName(), request.getCount());

        List<Administrator> allAdmins = administratorRepository.findAll();
        for (Administrator admin : allAdmins) {
            notificationService.notifyAdmin(admin.getId(), "Новая заявка на закупку", message);
        }

        return mapToRs(saved);
    }

    @Transactional
    public RequestBuyRs updateStatus(Long id, String status) {
        RequestBuy buy = requestBuyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        String oldStatus = buy.getStatus();
        buy.setStatus(status);
        RequestBuyRs result = mapToRs(requestBuyRepository.save(buy));

        String message = String.format("Заявка на закупку №%d: статус изменён с '%s' на '%s'",
                id, oldStatus, status);

        List<Administrator> allAdmins = administratorRepository.findAll();
        for (Administrator admin : allAdmins) {
            notificationService.notifyAdmin(admin.getId(), "Изменение статуса заявки на закупку", message);
        }

        return result;
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