package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.TORepairRq;
import com.fitnesscenter.app.dto.request.RequestRepairRq;
import com.fitnesscenter.app.dto.response.TORepairRs;
import com.fitnesscenter.app.dto.response.RequestRepairRs;
import com.fitnesscenter.app.dto.response.EquipmentHistoryRs;
import com.fitnesscenter.app.entity.Administrator;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.TORepair;
import com.fitnesscenter.app.entity.RequestRepair;
import com.fitnesscenter.app.repository.*;
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
public class TORepairService {
    private final TORepairRepository toRepairRepository;
    private final RequestRepairRepository requestRepairRepository;
    private final EquipmentHistoryRepository equipmentHistoryRepository;
    private final NotificationService notificationService;
    private final EquipmentRepository equipmentRepository;
    private final AdministratorRepository administratorRepository;

    // ========== МЕТОДЫ ДЛЯ ТО ==========

    public Page<TORepairRs> getAllTO(Pageable pageable) {
        return toRepairRepository.findAll(pageable)
                .map(this::mapToRs);
    }

    public List<TORepairRs> getAllTOList() {
        return toRepairRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public TORepairRs createTO(TORepairRq request) {
        List<TORepair> existingPlanned = toRepairRepository
                .findByEquipmentIdAndStatus(request.getEquipmentId(), "Запланировано");

        if (!existingPlanned.isEmpty()) {
            throw new RuntimeException("Для этого оборудования уже есть запланированное ТО");
        }

        TORepair toRepair = new TORepair();
        toRepair.setEquipmentId(request.getEquipmentId());
        toRepair.setType(request.getType());
        toRepair.setPlannedDate(request.getPlannedDate());
        toRepair.setDescription(request.getDescription());
        toRepair.setWorker(request.getWorker());
        toRepair.setStatus("Запланировано");

        TORepair saved = toRepairRepository.save(toRepair);

        String message = String.format("Создано новое плановое ТО для оборудования ID=%d на дату %s",
                request.getEquipmentId(), request.getPlannedDate());

        List<Administrator> allAdmins = administratorRepository.findAll();
        for (Administrator admin : allAdmins) {
            notificationService.notifyAdmin(admin.getId(), "Новое плановое ТО", message);
        }

        return mapToRs(saved);
    }

    @Transactional
    public TORepairRs completeTO(Long toId) {
        TORepair toRepair = toRepairRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("TO not found"));

        if ("Выполнена".equals(toRepair.getStatus())) {
            throw new RuntimeException("ТО уже выполнено");
        }

        toRepair.setStatus("Выполнена");
        toRepair.setCompletedDate(LocalDate.now());
        TORepair saved = toRepairRepository.save(toRepair);

        List<TORepair> existingPlanned = toRepairRepository
                .findByEquipmentIdAndStatus(saved.getEquipmentId(), "Запланировано");

        LocalDate nextDate = null;
        if (existingPlanned.isEmpty()) {
            nextDate = calculateNextTODate(saved.getEquipmentId());
            TORepair nextTO = new TORepair();
            nextTO.setEquipmentId(saved.getEquipmentId());
            nextTO.setType(saved.getType());
            nextTO.setPlannedDate(nextDate);
            nextTO.setDescription("Плановое ТО (автоматически)");
            nextTO.setStatus("Запланировано");
            toRepairRepository.save(nextTO);
        }

        String message = String.format("ТО №%d для оборудования ID=%d завершено. %s",
                toId, saved.getEquipmentId(),
                nextDate != null ? "Следующее ТО запланировано на " + nextDate : "");

        List<Administrator> allAdmins = administratorRepository.findAll();
        for (Administrator admin : allAdmins) {
            notificationService.notifyAdmin(admin.getId(), "Завершение ТО", message);
        }

        return mapToRs(saved);
    }

    // ========== МЕТОДЫ ДЛЯ ЗАЯВОК НА РЕМОНТ ==========

    public Page<RequestRepairRs> getRequestsByFilters(String type, LocalDate startDate, LocalDate endDate, Long equipmentId, Pageable pageable) {
        return requestRepairRepository.findAll(pageable)
                .map(this::mapToRs);
    }

    public List<RequestRepairRs> getAllRequestsList() {
        return requestRepairRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestRepairRs registerRepairRequest(RequestRepairRq request) {
        RequestRepair repair = new RequestRepair();
        repair.setEquipmentInventoryNumber(request.getEquipmentInventoryNumber());
        repair.setCreator(request.getCreator());
        repair.setDescription(request.getDescription());
        repair.setTORepairId(request.getTORepairId());
        repair.setStatus("Открыта");

        RequestRepair saved = requestRepairRepository.save(repair);

        String message = String.format("Создана новая заявка на ремонт для оборудования №%d от %s",
                request.getEquipmentInventoryNumber(), request.getCreator());

        List<Administrator> allAdmins = administratorRepository.findAll();
        for (Administrator admin : allAdmins) {
            notificationService.notifyAdmin(admin.getId(), "Новая заявка на ремонт", message);
        }

        return mapToRs(saved);
    }

    @Transactional
    public RequestRepairRs updateRequestStatus(Long requestId, String status, String worker) {
        RequestRepair repair = requestRepairRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        String oldStatus = repair.getStatus();
        repair.setStatus(status);
        repair.setWorker(worker);

        RequestRepairRs result = mapToRs(requestRepairRepository.save(repair));

        String message = String.format("Заявка на ремонт №%d: статус изменён с '%s' на '%s'",
                requestId, oldStatus, status);

        List<Administrator> allAdmins = administratorRepository.findAll();
        for (Administrator admin : allAdmins) {
            notificationService.notifyAdmin(admin.getId(), "Изменение статуса заявки на ремонт", message);
        }

        return result;
    }

    // ========== ОБЩИЕ МЕТОДЫ ==========

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
        List<TORepair> completedTOs = toRepairRepository.findByEquipmentIdAndStatus(equipmentId, "Выполнена");

        LocalDate lastCompletedDate = null;

        if (!completedTOs.isEmpty()) {
            TORepair lastTO = completedTOs.stream()
                    .filter(t -> t.getCompletedDate() != null)
                    .max((a, b) -> a.getCompletedDate().compareTo(b.getCompletedDate()))
                    .orElse(null);

            if (lastTO != null && lastTO.getCompletedDate() != null) {
                lastCompletedDate = lastTO.getCompletedDate();
            }
        }

        LocalDate nextDate;

        if (lastCompletedDate != null) {
            nextDate = lastCompletedDate.plusDays(90);
        } else {
            Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
            if (equipment != null && equipment.getDataBuy() != null) {
                nextDate = equipment.getDataBuy().plusDays(90);
            } else {
                nextDate = LocalDate.now().plusDays(90);
            }
        }

        return nextDate;
    }

    // ========== MAPPER МЕТОДЫ ==========

    private TORepairRs mapToRs(TORepair entity) {
        return TORepairRs.builder()
                .id(entity.getId())
                .equipmentId(entity.getEquipmentId())
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
                .created_at(entity.getCreatedAt())
                .status(entity.getStatus())
                .worker(entity.getWorker())
                .description(entity.getDescription())
                .creator(entity.getCreator())
                .build();
    }
}