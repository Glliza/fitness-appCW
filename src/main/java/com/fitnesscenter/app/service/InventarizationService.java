package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.response.InventarizationRs;
import com.fitnesscenter.app.dto.response.InventarizationReportRs;
import com.fitnesscenter.app.dto.response.InventarizationAllRs;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.Inventarization;
import com.fitnesscenter.app.entity.Zone;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.repository.EquipmentRepository;
import com.fitnesscenter.app.repository.InventarizationRepository;
import com.fitnesscenter.app.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarizationService {
    private final InventarizationRepository inventarizationRepository;
    private final EquipmentRepository equipmentRepository;
    private final ZoneRepository zoneRepository;

    // Генерация уникального ID сессии
    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    // ИНВЕНТАРИЗАЦИЯ ПО ЗОНЕ
    @Transactional
    public List<InventarizationRs> startInventarization(Long zoneId) {
        String sessionId = generateSessionId();
        List<Equipment> equipmentList = equipmentRepository.findByZoneIdAndDeletedFalse(zoneId);

        if (equipmentList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Inventarization> records = new ArrayList<>();
        for (Equipment eq : equipmentList) {
            Inventarization inv = new Inventarization();
            inv.setEquipmentInventoryNumber(eq.getId());
            inv.setZoneId(zoneId);
            inv.setCount(1);
            inv.setRealCount(null);
            inv.setDate(LocalDate.now());
            inv.setSessionId(sessionId);
            records.add(inv);
        }

        return inventarizationRepository.saveAll(records).stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    // ИНВЕНТАРИЗАЦИЯ ПО ВСЕМУ ОБОРУДОВАНИЮ
    @Transactional
    public List<InventarizationAllRs> startInventarizationAll() {
        String sessionId = generateSessionId();
        List<Equipment> equipmentList = equipmentRepository.findAllByDeletedFalse();

        if (equipmentList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Inventarization> records = new ArrayList<>();
        for (Equipment eq : equipmentList) {
            Inventarization inv = new Inventarization();
            inv.setEquipmentInventoryNumber(eq.getId());
            inv.setZoneId(eq.getZoneId());
            inv.setCount(1);
            inv.setRealCount(null);
            inv.setDate(LocalDate.now());
            inv.setSessionId(sessionId);
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

    // ВЫПОЛНИТЬ ШАГ
    @Transactional
    public InventarizationRs performStep(Long inventarizationId, Integer actualCount) {
        Inventarization inv = inventarizationRepository.findById(inventarizationId)
                .orElseThrow(() -> new EntityNotFoundException("Inventarization", inventarizationId));

        // Если у старой записи нет sessionId, генерируем его, чтобы не ломать логику
        if (inv.getSessionId() == null) {
            inv.setSessionId(generateSessionId());
        }

        inv.setRealCount(actualCount);
        return mapToRs(inventarizationRepository.save(inv));
    }

    // ЗАВЕРШИТЬ ИНВЕНТАРИЗАЦИЮ ПО ЗОНЕ
    @Transactional
    public InventarizationReportRs finishInventarization(Long zoneId) {
        List<Inventarization> allZoneInv = inventarizationRepository.findByZoneId(zoneId);

        if (allZoneInv.isEmpty()) {
            String zoneName = getZoneName(zoneId);
            return InventarizationReportRs.builder()
                    .zoneId(zoneId)
                    .zoneName(zoneName)
                    .discrepancies(new ArrayList<>())
                    .totalScanned(0)
                    .date(LocalDate.now())
                    .build();
        }

        // Сортируем по ID desc, чтобы взять самую свежую запись и её sessionId
        allZoneInv.sort((a, b) -> b.getId().compareTo(a.getId()));

        Inventarization latestInv = allZoneInv.get(0);
        String currentSessionId = latestInv.getSessionId();

        // Защита от null sessionId для старых данных
        if (currentSessionId == null) {
            currentSessionId = generateSessionId();
            latestInv.setSessionId(currentSessionId);
            inventarizationRepository.save(latestInv);
        }

        final String finalSessionId = currentSessionId;
        LocalDate date = latestInv.getDate();

        // Фильтруем записи только текущей сессии. Используем Objects.equals для безопасности
        List<Inventarization> sessionInvs = allZoneInv.stream()
                .filter(inv -> Objects.equals(inv.getSessionId(), finalSessionId))
                .collect(Collectors.toList());

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

        String zoneName = getZoneName(zoneId);

        return InventarizationReportRs.builder()
                .zoneId(zoneId)
                .zoneName(zoneName)
                .discrepancies(discrepancies)
                .totalScanned(totalScanned)
                .date(date)
                .sessionId(finalSessionId)
                .build();
    }

    // ЗАВЕРШИТЬ ИНВЕНТАРИЗАЦИЮ ПО ВСЕМУ ОБОРУДОВАНИЮ
    @Transactional
    public InventarizationReportRs finishInventarizationAll() {
        List<Inventarization> allInventarizations = inventarizationRepository.findAll();

        if (allInventarizations.isEmpty()) {
            return InventarizationReportRs.builder()
                    .zoneId(null)
                    .zoneName("Все зоны")
                    .discrepancies(new ArrayList<>())
                    .totalScanned(0)
                    .date(LocalDate.now())
                    .build();
        }

        allInventarizations.sort((a, b) -> b.getId().compareTo(a.getId()));

        Inventarization latestInv = allInventarizations.get(0);
        String currentSessionId = latestInv.getSessionId();

        if (currentSessionId == null) {
            currentSessionId = generateSessionId();
            latestInv.setSessionId(currentSessionId);
            inventarizationRepository.save(latestInv);
        }

        final String finalSessionId = currentSessionId;
        LocalDate date = latestInv.getDate();

        List<Inventarization> sessionInvs = allInventarizations.stream()
                .filter(inv -> Objects.equals(inv.getSessionId(), finalSessionId))
                .collect(Collectors.toList());

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

        return InventarizationReportRs.builder()
                .zoneId(null)
                .zoneName("Все зоны")
                .discrepancies(discrepancies)
                .totalScanned(totalScanned)
                .date(date)
                .sessionId(finalSessionId)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<InventarizationReportRs> getAllInventarizationHistory(Pageable pageable) {
        List<Inventarization> allInventarizations = inventarizationRepository.findAll();

        if (allInventarizations.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // Группируем по sessionId
        Map<String, List<Inventarization>> groupedBySession = allInventarizations.stream()
                .collect(Collectors.groupingBy(inv ->
                        inv.getSessionId() != null ? inv.getSessionId() : "LEGACY_" + inv.getId()
                ));

        List<InventarizationReportRs> history = new ArrayList<>();

        for (Map.Entry<String, List<Inventarization>> entry : groupedBySession.entrySet()) {
            List<Inventarization> sessionInvs = entry.getValue();
            if (sessionInvs.isEmpty()) continue;

            // Находим минимальный ID в этой сессии.
            // Это будет служить меткой времени создания всей инвентаризации.
            Long minIdInSession = sessionInvs.stream()
                    .map(Inventarization::getId)
                    .min(Long::compareTo)
                    .orElse(0L);

            Inventarization first = sessionInvs.get(0);
            Long zoneId = first.getZoneId();
            LocalDate date = first.getDate();
            String sessionId = entry.getKey();

            Set<Long> zonesInSession = sessionInvs.stream()
                    .map(Inventarization::getZoneId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            boolean isAllMode = zonesInSession.size() > 1;

            String zoneName;
            Long displayZoneId;

            if (isAllMode) {
                displayZoneId = null;
                zoneName = "Все зоны";
            } else {
                displayZoneId = zonesInSession.isEmpty() ? null : zonesInSession.iterator().next();
                zoneName = getZoneName(displayZoneId);
            }

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

            // Добавляем минимальный ID в объект отчета, чтобы использовать его для сортировки
            // Мы используем поле date временно или добавим новое поле в DTO, но проще отсортировать список объектов напрямую

            // Создаем временную обертку или просто сохраняем minId для сортировки
            // Так как InventarizationReportRs не имеет поля minId, мы отсортируем список history позже,

            history.add(InventarizationReportRs.builder()
                    .zoneId(displayZoneId)
                    .zoneName(zoneName)
                    .discrepancies(discrepancies)
                    .totalScanned(totalScanned)
                    .date(date)
                    .sessionId(sessionId)
                    .build());

            // Сохраняем связь sessionId и минимального ID для сортировки
            // Для этого создадим вспомогательную карту внутри метода или изменим подход
        }

        // --- ИСПРАВЛЕННАЯ ЛОГИКА СОРТИРОВКИ ---

        // 1. Создаем карту: SessionId -> Минимальный ID записи в этой сессии
        Map<String, Long> sessionToMinIdMap = new HashMap<>();
        for (Map.Entry<String, List<Inventarization>> entry : groupedBySession.entrySet()) {
            Long minId = entry.getValue().stream()
                    .map(Inventarization::getId)
                    .min(Long::compareTo)
                    .orElse(0L);
            sessionToMinIdMap.put(entry.getKey(), minId);
        }

        // 2. Сортируем список history по минимальному ID сессии (возрастание)
        history.sort((a, b) -> {
            Long minIdA = sessionToMinIdMap.get(a.getSessionId());
            Long minIdB = sessionToMinIdMap.get(b.getSessionId());
            return minIdA.compareTo(minIdB);
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), history.size());

        if (start > history.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, history.size());
        }

        return new PageImpl<>(history.subList(start, end), pageable, history.size());
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

    // Вспомогательный метод для получения имени зоны
    private String getZoneName(Long zoneId) {
        if (zoneId == null) return "Неизвестно";
        var zone = zoneRepository.findById(zoneId).orElse(null);
        return zone != null ? zone.getName() : "Зона " + zoneId;
    }
}