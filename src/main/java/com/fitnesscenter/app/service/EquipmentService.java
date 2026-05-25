package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.request.EquipmentRq;
import com.fitnesscenter.app.dto.response.EquipmentRs;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.Zone;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.repository.*;
import lombok.RequiredArgsConstructor;
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

    public List<EquipmentRs> getAllEquipment() {
        return equipmentRepository.findAllByDeletedFalse().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipmentRs createEquipment(EquipmentRq request) {
        Zone zone = zoneRepository.findByIdAndDeletedFalse(request.getZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Zone", request.getZoneId()));

        Equipment equipment = new Equipment();

        equipment.setZoneId(request.getZoneId());
        equipment.setName(request.getName());
        equipment.setStatus("Новое");
        equipment.setDataBuy(request.getDataBuy());

        Equipment saved = equipmentRepository.save(equipment);
        return mapToRs(saved);
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

    public List<EquipmentRs> getByZone(Long zoneId) {
        return equipmentRepository.findByZoneIdAndDeletedFalse(zoneId).stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    public List<EquipmentRs> getByStatus(String status) {
        return equipmentRepository.findByStatusAndDeletedFalse(status).stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    private EquipmentRs mapToRs(Equipment equipment) {
        String zoneName = zoneRepository.findByIdAndDeletedFalse(equipment.getZoneId())
                .map(Zone::getName)
                .orElse("");

        return EquipmentRs.builder()
                .id(equipment.getId())
                .zoneName(zoneName)
                .name(equipment.getName())
                .status(equipment.getStatus())
                .dataBuy(equipment.getDataBuy())
                .build();
    }
}