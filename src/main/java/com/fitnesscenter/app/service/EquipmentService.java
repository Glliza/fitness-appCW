package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.EquipmentRq;
import com.fitnesscenter.app.dto.response.EquipmentRs;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.Zone;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final ZoneRepository zoneRepository;
    private final EquipmentHistoryRepository equipmentHistoryRepository;
    private final InventarizationRepository inventarizationRepository;
    private final RequestRepairRepository requestRepairRepository;

    public EquipmentRs getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", id));
        return mapToRs(equipment);
    }

    // Новый метод с пагинацией для всех
    public Page<EquipmentRs> getAllEquipment(Pageable pageable) {
        return equipmentRepository.findAllByDeletedFalse(pageable)
                .map(this::mapToRs);
    }

    // Новый метод с пагинацией и фильтрацией по зоне
    public Page<EquipmentRs> getEquipmentByZone(Long zoneId, Pageable pageable) {
        return equipmentRepository.findByZoneIdAndDeletedFalse(zoneId, pageable)
                .map(this::mapToRs);
    }

    // Новый метод с пагинацией и фильтрацией по статусу
    public Page<EquipmentRs> getEquipmentByStatus(String status, Pageable pageable) {
        return equipmentRepository.findByStatusAndDeletedFalse(status, pageable)
                .map(this::mapToRs);
    }

    // Новый метод с пагинацией и фильтрацией по зоне и статусу
    public Page<EquipmentRs> getEquipmentByZoneAndStatus(Long zoneId, String status, Pageable pageable) {
        return equipmentRepository.findByZoneIdAndStatusAndDeletedFalse(zoneId, status, pageable)
                .map(this::mapToRs);
    }

    // Старые методы оставляем для совместимости
    public List<EquipmentRs> getAllEquipmentList() {
        return equipmentRepository.findAllByDeletedFalse().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipmentRs createEquipment(EquipmentRq request) {
        System.out.println("=== СОЗДАНИЕ ОБОРУДОВАНИЯ ===");
        System.out.println("zoneId: " + request.getZoneId());
        System.out.println("name: " + request.getName());

        checkZoneCapacity(request.getZoneId());

        Zone zone = zoneRepository.findByIdAndDeletedFalse(request.getZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Zone", request.getZoneId()));

        Equipment equipment = new Equipment();
        equipment.setZoneId(request.getZoneId());
        equipment.setName(request.getName());
        equipment.setStatus(request.getStatus() != null ? request.getStatus() : "Новое");
        equipment.setDataBuy(request.getDataBuy());

        Equipment saved = equipmentRepository.save(equipment);
        System.out.println("Сохранённое оборудование - ID: " + saved.getId());
        return mapToRs(saved);
    }

    @Transactional
    public EquipmentRs updateEquipment(Long id, EquipmentRq request) {
        Equipment equipment = equipmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", id));

        Long oldZoneId = equipment.getZoneId();
        Long newZoneId = request.getZoneId();

        if (newZoneId != null && !newZoneId.equals(oldZoneId)) {
            checkZoneCapacity(newZoneId);
            equipment.setZoneId(newZoneId);
        }

        equipment.setName(request.getName());
        equipment.setStatus(request.getStatus());
        equipment.setDataBuy(request.getDataBuy());

        return mapToRs(equipmentRepository.save(equipment));
    }

    @Transactional
    public EquipmentRs changeStatus(Long id, String newStatus) {
        Equipment equipment = equipmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", id));

        equipment.setStatus(newStatus);

        return mapToRs(equipmentRepository.save(equipment));
    }

    @Transactional
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment", id));
        equipment.setDeleted(true);
        equipmentRepository.save(equipment);
    }

    public List<EquipmentRs> getByZoneList(Long zoneId) {
        return equipmentRepository.findByZoneIdAndDeletedFalse(zoneId).stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    public List<EquipmentRs> getByStatusList(String status) {
        return equipmentRepository.findByStatusAndDeletedFalse(status).stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    private EquipmentRs mapToRs(Equipment equipment) {
        String zoneName = "";
        if (equipment.getZoneId() != null) {
            zoneName = zoneRepository.findByIdAndDeletedFalse(equipment.getZoneId())
                    .map(Zone::getName)
                    .orElse("");
        }

        return EquipmentRs.builder()
                .id(equipment.getId())
                .zoneId(equipment.getZoneId())
                .zoneName(zoneName)
                .name(equipment.getName())
                .status(equipment.getStatus())
                .dataBuy(equipment.getDataBuy())
                .build();
    }

    private void checkZoneCapacity(Long zoneId) {
        Zone zone = zoneRepository.findByIdAndDeletedFalse(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone", zoneId));

        Integer currentEquipmentCount = equipmentRepository.countByZoneIdAndDeletedFalse(zoneId);

        if (currentEquipmentCount >= zone.getCapacity()) {
            throw new RuntimeException("Невозможно добавить оборудование. Зона '" + zone.getName() +
                    "' имеет вместимость " + zone.getCapacity() +
                    " единиц оборудования. Уже занято: " + currentEquipmentCount);
        }
    }
}