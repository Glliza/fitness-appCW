package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.TORepairRq;
import com.fitnesscenter.app.dto.request.RequestRepairRq;
import com.fitnesscenter.app.dto.response.TORepairRs;
import com.fitnesscenter.app.dto.response.RequestRepairRs;
import com.fitnesscenter.app.dto.response.EquipmentHistoryRs;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.TORepair;
import com.fitnesscenter.app.entity.RequestRepair;
import com.fitnesscenter.app.repository.EquipmentRepository;
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
    private final EquipmentRepository equipmentRepository;

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
        // Проверяем, нет ли уже запланированного ТО для этого оборудования
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
        return mapToRs(saved);
    }

    @Transactional
    public RequestRepairRs registerRepairRequest(RequestRepairRq request) {
        RequestRepair repair = new RequestRepair();
        repair.setEquipmentInventoryNumber(request.getEquipmentInventoryNumber());
        repair.setCreator(request.getCreator());
        repair.setDescription(request.getDescription());  // ДОБАВЬТЕ ЭТУ СТРОКУ
        repair.setTORepairId(request.getTORepairId());
        repair.setStatus("Открыta");

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
        System.out.println("=== РАСЧЁТ ДАТЫ СЛЕДУЮЩЕГО ТО ===");
        System.out.println("Equipment ID: " + equipmentId);

        // Находим последнее ВЫПОЛНЕННОЕ ТО (завершённое)
        List<TORepair> completedTOs = toRepairRepository.findByEquipmentIdAndStatus(equipmentId, "Выполнена");

        LocalDate lastCompletedDate = null;

        if (!completedTOs.isEmpty()) {
            // Сортируем по дате выполнения (completedDate) и берём последнюю
            TORepair lastTO = completedTOs.stream()
                    .filter(t -> t.getCompletedDate() != null)
                    .max((a, b) -> a.getCompletedDate().compareTo(b.getCompletedDate()))
                    .orElse(null);

            if (lastTO != null && lastTO.getCompletedDate() != null) {
                lastCompletedDate = lastTO.getCompletedDate();
                System.out.println("Последняя выполненная дата: " + lastCompletedDate);
            }
        }

        LocalDate nextDate;

        if (lastCompletedDate != null) {
            // Прибавляем 90 дней к последней выполненной дате
            nextDate = lastCompletedDate.plusDays(90);
            System.out.println("Следующая дата от последнего ТО: " + nextDate);
        } else {
            // Если нет выполненных ТО, берём дату покупки оборудования
            Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
            if (equipment != null && equipment.getDataBuy() != null) {
                nextDate = equipment.getDataBuy().plusDays(90);
                System.out.println("Следующая дата от даты покупки (" + equipment.getDataBuy() + "): " + nextDate);
            } else {
                nextDate = LocalDate.now().plusDays(90);
                System.out.println("Следующая дата от сегодня: " + nextDate);
            }
        }

        return nextDate;
    }

    private TORepairRs mapToRs(TORepair entity) {
        return TORepairRs.builder()
                .id(entity.getId())
                .equipmentId(entity.getEquipmentId())  // ДОБАВЬТЕ
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
                .description(entity.getDescription())  // ДОБАВЬТЕ ЭТУ СТРОКУ
                .creator(entity.getCreator())
                .build();
    }

    @Transactional
    public TORepairRs completeTO(Long toId) {
        System.out.println("=== ЗАВЕРШЕНИЕ ТО ===");
        System.out.println("TO ID: " + toId);

        TORepair toRepair = toRepairRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("TO not found"));

        System.out.println("Текущий статус: " + toRepair.getStatus());
        System.out.println("Equipment ID: " + toRepair.getEquipmentId());

        if ("Выполнена".equals(toRepair.getStatus())) {
            throw new RuntimeException("ТО уже выполнено");
        }

        toRepair.setStatus("Выполнена");
        toRepair.setCompletedDate(LocalDate.now());
        TORepair saved = toRepairRepository.save(toRepair);

        System.out.println("Установлена дата завершения: " + saved.getCompletedDate());

        // Проверяем, есть ли уже запланированное ТО
        List<TORepair> existingPlanned = toRepairRepository
                .findByEquipmentIdAndStatus(saved.getEquipmentId(), "Запланировано");

        System.out.println("Существующих запланированных ТО: " + existingPlanned.size());

        if (existingPlanned.isEmpty()) {
            LocalDate nextDate = calculateNextTODate(saved.getEquipmentId());
            System.out.println("Создаём новое ТО на дату: " + nextDate);

            TORepair nextTO = new TORepair();
            nextTO.setEquipmentId(saved.getEquipmentId());
            nextTO.setType(saved.getType());
            nextTO.setPlannedDate(nextDate);
            nextTO.setDescription("Плановое ТО (автоматически)");
            nextTO.setStatus("Запланировано");
            toRepairRepository.save(nextTO);
        }

        return mapToRs(saved);
    }

    public List<TORepairRs> getAllTO() {
        return toRepairRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }
}