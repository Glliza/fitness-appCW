package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.response.InventarizationRs;
import com.fitnesscenter.app.dto.response.InventarizationReportRs;
import com.fitnesscenter.app.dto.response.InventarizationAllRs;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.Inventarization;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.repository.EquipmentRepository;
import com.fitnesscenter.app.repository.InventarizationRepository;
import com.fitnesscenter.app.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarizationService {
    private final InventarizationRepository inventarizationRepository;
    private final EquipmentRepository equipmentRepository;
    private final ZoneRepository zoneRepository;

    @Transactional
    public List<InventarizationRs> startInventarization(Long zoneId) {
        // Сначала удаляем старые незавершённые инвентаризации для этой зоны
        List<Inventarization> oldInvs = inventarizationRepository.findByZoneId(zoneId);
        if (!oldInvs.isEmpty()) {
            inventarizationRepository.deleteAll(oldInvs);
        }

        List<Equipment> equipmentList = equipmentRepository.findByZoneIdAndDeletedFalse(zoneId);
        List<Inventarization> records = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            Inventarization inv = new Inventarization();
            inv.setEquipmentInventoryNumber(eq.getId());
            inv.setZoneId(zoneId);
            inv.setCount(1);
            inv.setRealCount(null);
            inv.setDate(LocalDate.now());
            records.add(inv);
        }

        List<Inventarization> saved = inventarizationRepository.saveAll(records);
        return saved.stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<InventarizationAllRs> startInventarization() {
        // Сначала удаляем все старые незавершённые инвентаризации
        inventarizationRepository.deleteAll();

        List<Equipment> equipmentList = equipmentRepository.findAllByDeletedFalse();
        List<Inventarization> records = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            Inventarization inv = new Inventarization();
            inv.setEquipmentInventoryNumber(eq.getId());
            inv.setZoneId(eq.getZoneId());
            inv.setCount(1);
            inv.setRealCount(null);
            inv.setDate(LocalDate.now());
            records.add(inv);
        }

        List<Inventarization> savedRecords = inventarizationRepository.saveAll(records);

        Map<Long, List<Inventarization>> byZone = savedRecords.stream()
                .collect(Collectors.groupingBy(Inventarization::getZoneId));

        List<InventarizationAllRs> result = new ArrayList<>();
        for (Map.Entry<Long, List<Inventarization>> entry : byZone.entrySet()) {
            String zoneName = "Неизвестно";
            if (entry.getKey() != null) {
                var zone = zoneRepository.findById(entry.getKey()).orElse(null);
                if (zone != null) zoneName = zone.getName();
            }

            List<InventarizationRs> items = entry.getValue().stream()
                    .map(this::mapToRs)
                    .collect(Collectors.toList());

            result.add(InventarizationAllRs.builder()
                    .zoneId(entry.getKey())
                    .zoneName(zoneName)
                    .items(items)
                    .build());
        }
        return result;
    }

    @Transactional
    public InventarizationRs performStep(Long inventarizationId, Integer actualCount) {
        Inventarization inv = inventarizationRepository.findById(inventarizationId)
                .orElseThrow(() -> new EntityNotFoundException("Inventarization", inventarizationId));

        inv.setRealCount(actualCount);
        return mapToRs(inventarizationRepository.save(inv));
    }

    @Transactional
    public InventarizationReportRs finishInventarization(Long zoneId) {
        List<Inventarization> zoneInventarizations = inventarizationRepository.findByZoneId(zoneId);

        List<String> discrepancies = new ArrayList<>();
        int totalScanned = 0;

        for (Inventarization inv : zoneInventarizations) {
            Equipment eq = equipmentRepository.findById(inv.getEquipmentInventoryNumber()).orElse(null);
            String equipmentName = eq != null ? eq.getName() : "Оборудование №" + inv.getEquipmentInventoryNumber();

            if (inv.getRealCount() != null) {
                totalScanned++;
                if (!inv.getCount().equals(inv.getRealCount())) {
                    discrepancies.add(String.format("%s: ожидалось %d, фактически %d",
                            equipmentName, inv.getCount(), inv.getRealCount()));
                }
            } else {
                discrepancies.add(String.format("%s: не проверено", equipmentName));
            }
        }

        return InventarizationReportRs.builder()
                .zoneId(zoneId)
                .discrepancies(discrepancies)
                .totalScanned(totalScanned)
                .date(LocalDate.now())
                .build();
    }

    @Transactional
    public InventarizationReportRs finishInventarization() {
        List<Inventarization> allInventarizations = inventarizationRepository.findAll();

        List<String> discrepancies = new ArrayList<>();
        int totalScanned = 0;

        Map<Long, List<Inventarization>> byZone = allInventarizations.stream()
                .collect(Collectors.groupingBy(Inventarization::getZoneId));

        for (Map.Entry<Long, List<Inventarization>> entry : byZone.entrySet()) {
            String zoneName = "Неизвестно";
            if (entry.getKey() != null) {
                var zone = zoneRepository.findById(entry.getKey()).orElse(null);
                if (zone != null) zoneName = zone.getName();
            }
            discrepancies.add("=== Зона: " + zoneName + " ===");

            for (Inventarization inv : entry.getValue()) {
                Equipment eq = equipmentRepository.findById(inv.getEquipmentInventoryNumber()).orElse(null);
                String equipmentName = eq != null ? eq.getName() : "Оборудование №" + inv.getEquipmentInventoryNumber();

                if (inv.getRealCount() != null) {
                    totalScanned++;
                    if (!inv.getCount().equals(inv.getRealCount())) {
                        discrepancies.add(String.format("  %s: ожидалось %d, фактически %d",
                                equipmentName, inv.getCount(), inv.getRealCount()));
                    }
                } else {
                    discrepancies.add(String.format("  %s: не проверено", equipmentName));
                }
            }
        }

        return InventarizationReportRs.builder()
                .zoneId(null)
                .discrepancies(discrepancies)
                .totalScanned(totalScanned)
                .date(LocalDate.now())
                .build();
    }

    private InventarizationRs mapToRs(Inventarization entity) {
        return InventarizationRs.builder()
                .id(entity.getId())
                .equipmentInventoryNumber(entity.getEquipmentInventoryNumber())
                .count(entity.getCount())
                .realCount(entity.getRealCount())
                .date(entity.getDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<InventarizationReportRs> getAllInventarizationHistory() {
        // Получаем все завершённые инвентаризации, сгруппированные по дате/зоне
        List<Inventarization> allInventarizations = inventarizationRepository.findAll();

        if (allInventarizations.isEmpty()) {
            return new ArrayList<>();
        }

        // Группируем по зоне и дате (каждая инвентаризация - это отдельный отчёт)
        Map<String, List<Inventarization>> groupedBySession = allInventarizations.stream()
                .collect(Collectors.groupingBy(inv -> inv.getZoneId() + "_" + inv.getDate()));

        List<InventarizationReportRs> history = new ArrayList<>();

        for (Map.Entry<String, List<Inventarization>> entry : groupedBySession.entrySet()) {
            List<Inventarization> sessionInvs = entry.getValue();
            Long zoneId = sessionInvs.get(0).getZoneId();
            LocalDate date = sessionInvs.get(0).getDate();

            List<String> discrepancies = new ArrayList<>();
            int totalScanned = 0;

            for (Inventarization inv : sessionInvs) {
                Equipment eq = equipmentRepository.findById(inv.getEquipmentInventoryNumber()).orElse(null);
                String equipmentName = eq != null ? eq.getName() : "Оборудование №" + inv.getEquipmentInventoryNumber();

                if (inv.getRealCount() != null) {
                    totalScanned++;
                    if (!inv.getCount().equals(inv.getRealCount())) {
                        discrepancies.add(String.format("%s: ожидалось %d, фактически %d",
                                equipmentName, inv.getCount(), inv.getRealCount()));
                    }
                }
            }

            history.add(InventarizationReportRs.builder()
                    .zoneId(zoneId)
                    .discrepancies(discrepancies)
                    .totalScanned(totalScanned)
                    .date(date)
                    .build());
        }

        // Сортируем по дате (сначала новые)
        history.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        return history;
    }
}