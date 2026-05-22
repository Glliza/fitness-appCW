package com.fitnesscenter.app.entity;

import com.fitnesscenter.app.enums.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "request_repair")
public class RequestRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "torepair_id")
    private Long torepairId;

    @Column(name = "equipment_inventory_number", length = 255)
    private String equipmentInventoryNumber;

    @Column(name = "created_at")
    private LocalDate createdAt;

    private String status;

    private String worker;

    private String description;

    private String creator;

    @ManyToOne
    @JoinColumn(name = "equipment_inventory_number", referencedColumnName = "inventory_number", insertable = false, updatable = false)
    private Equipment equipment;

    @OneToOne
    @JoinColumn(name = "torepair_id", insertable = false, updatable = false)
    private TORepair torepair;

    public RequestRepair() {
        this.createdAt = LocalDate.now();
        this.status = RequestStatus.OPENED.name();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTorepairId() { return torepairId; }
    public void setTorepairId(Long torepairId) { this.torepairId = torepairId; }

    public String getEquipmentInventoryNumber() { return equipmentInventoryNumber; }
    public void setEquipmentInventoryNumber(String equipmentInventoryNumber) { this.equipmentInventoryNumber = equipmentInventoryNumber; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWorker() { return worker; }
    public void setWorker(String worker) { this.worker = worker; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public TORepair getTorepair() { return torepair; }
    public void setTorepair(TORepair torepair) { this.torepair = torepair; }
}