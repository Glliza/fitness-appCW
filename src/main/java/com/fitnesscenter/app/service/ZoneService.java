package com.fitnesscenter.app.service;


import com.fitnesscenter.app.dto.request.ZoneRq;
import com.fitnesscenter.app.dto.response.ZoneRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.entity.Zone;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.exception.ZoneHasEquipmentException;
import com.fitnesscenter.app.repository.EquipmentRepository;
import com.fitnesscenter.app.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;
    private final EquipmentRepository equipmentRepository;

    public ZoneRs getZoneById(Long id) {
        Zone zone = zoneRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone", id));
        return mapToRs(zone);
    }

    public List<ZoneRs> getAllZones() {
        return zoneRepository.findAllByDeletedFalse().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public ZoneRs createZone(ZoneRq request) {
        if (zoneRepository.existsByNameAndDeletedFalse(request.getName())) {
            throw new RuntimeException("Zone name already exists");
        }

        Zone zone = new Zone();
        zone.setUserid(request.getUserid());
        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setCapacity(request.getCapacity());
        zone.setFloor(request.getFloor());

        return mapToRs(zoneRepository.save(zone));
    }

    @Transactional
    public ZoneRs updateZone(ZoneRq request, Long id) {
        Zone zone = zoneRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone", id));

        if (!zone.getName().equals(request.getName()) &&
                zoneRepository.existsByNameAndDeletedFalse(request.getName())) {
            throw new RuntimeException("Zone name already exists");
        }

        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setCapacity(request.getCapacity());
        zone.setFloor(request.getFloor());

        return mapToRs(zoneRepository.save(zone));
    }

    @Transactional
    public void deleteZone(Long id) {
        Zone zone = zoneRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone", id));

        Integer equipmentCount = equipmentRepository.countByZoneIdAndDeletedFalse(id);
        if (equipmentCount > 0) {
            throw new ZoneHasEquipmentException(id, zone.getName(), equipmentCount);
        }

        zone.setDeleted(true);
        zoneRepository.save(zone);
    }

    public List<ConsumablesZonesRs> getConsumablesByZone(Long zoneId) {
        // логика получения расходников по зоне
        return List.of();
    }

    private ZoneRs mapToRs(Zone zone) {
        return ZoneRs.builder()
                .id(zone.getId())
                .userid(zone.getUserid())
                .name(zone.getName())
                .description(zone.getDescription())
                .capacity(zone.getCapacity())
                .floor(zone.getFloor())
                .build();
    }
}
