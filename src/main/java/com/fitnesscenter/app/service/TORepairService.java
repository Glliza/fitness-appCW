package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.TORepairRq;
import com.fitnesscenter.app.dto.request.RequestRepairRq;
import com.fitnesscenter.app.dto.response.TORepairRs;
import com.fitnesscenter.app.dto.response.RequestRepairRs;
import com.fitnesscenter.app.dto.response.EquipmentHistoryRs;
import com.fitnesscenter.app.entity.TORepair;
import com.fitnesscenter.app.entity.RequestRepair;
import com.fitnesscenter.app.repository.TORepairRepository;
import com.fitnesscenter.app.repository.RequestRepairRepository;
import com.fitnesscenter.app.repository.EquipmentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TORepairService {
    private final TORepairRepository toRepairRepository;
    private final RequestRepairRepository requestRepairRepository;
    private final EquipmentHistoryRepository equipmentHistoryRepository;
    private final NotificationService notificationService;

    @Transactional
    public RequestRepairRs updateRequestStatus(Long requestId, String status, String worker) {
        RequestRepair repair = requestRepairRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String oldStatus = repair.getStatus();
        repair.setStatus(status);
        repair.setWorker(worker);

        RequestRepairRs result = mapToRs(requestRepairRepository.save(repair));

        // Уведомляем администратора об изменении статуса
        String message = String.format("Заявка №%d: статус изменён с '%s' на '%s'",
                requestId, oldStatus, status);
        notificationService.notifyAdmin(1L, "Изменение статуса заявки", message);

        return result;
    }

    @Transactional
    public TORepairRs createTO(TORepairRq request) {
        TORepair toRepair = new TORepair();
        toRepair.setType(request.getType());
        toRepair.setPlannedDate(request.getPlannedDate());
        toRepair.setDescription(request.getDescription());
        toRepair.setWorker(request.getWorker());
        toRepair.setStatus("Запланировано");

        TORepair saved = toRepairRepository.save(toRepair);
        return mapToRs(saved);
    }

    @Transactional
    public RequestRepairRs registerRepairRequest(RequestRepairRq request) {
        RequestRepair repair = new RequestRepair();
        repair.setEquipmentInventoryNumber(request.getEquipmentInventoryNumber());
        repair.setCreator(request.getCreator());
        repair.setTORepairId(request.getTORepairId());
        // Используем createdAt из BaseEntity (устанавливается автоматически через @PrePersist)
        // repair.setCreatedAt(LocalDateTime.now()); - не нужно, BaseEntity сам установит
        repair.setStatus("Открыта");

        RequestRepair saved = requestRepairRepository.save(repair);
        return mapToRs(saved);
    }


    public void notifyAdmin(Long requestId) {
        // уведомление администратора
    }

    public List<RequestRepairRs> getRequestsByFilters(String type, LocalDate startDate, LocalDate endDate, Long equipmentId) {
        return requestRepairRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    public List<EquipmentHistoryRs> getMaintenanceHistory(Long equipmentId) {
        return equipmentHistoryRepository.findByEquipmentHistoryNumber(String.valueOf(equipmentId)).stream()
                .map(history -> EquipmentHistoryRs.builder()
                        .id(history.getId())
                        .equipmentHistoryNumber(history.getEquipmentHistoryNumber())
                        .type(history.getType())
                        .date(history.getDate())
                        .worker(history.getWorker())
                        .description(history.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    public LocalDate calculateNextTODate(Long equipmentId) {
        return LocalDate.now().plusDays(90);
    }

    private TORepairRs mapToRs(TORepair entity) {
        return TORepairRs.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .worker(entity.getWorker())
                .plannedDate(entity.getPlannedDate())
                .completedDate(entity.getCompletedDate())
                .type(entity.getType())
                .build();
    }

    private RequestRepairRs mapToRs(RequestRepair entity) {
        return RequestRepairRs.builder()
                .id(entity.getId())
                .TORepairId(entity.getTORepairId())
                .equipmentInventoryNumber(entity.getEquipmentInventoryNumber())
                .created_at(entity.getCreatedAt())  // Используем getCreatedAt() из BaseEntity
                .status(entity.getStatus())
                .worker(entity.getWorker())
                .description(entity.getDescription())
                .creator(entity.getCreator())
                .build();
    }
}