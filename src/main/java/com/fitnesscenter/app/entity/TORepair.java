package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "torepair")
public class TORepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    private String description;

    private String worker;

    private LocalDate plannedDate;

    private LocalDate completedDate;

    private String type;

    @OneToOne
    @JoinColumn(name = "equipment_inventory_number", referencedColumnName = "inventory_number")
    private Equipment equipment;

    @OneToOne(mappedBy = "torepair")
    private RequestRepair requestRepair;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWorker() { return worker; }
    public void setWorker(String worker) { this.worker = worker; }

    public LocalDate getPlannedDate() { return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate) { this.plannedDate = plannedDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public RequestRepair getRequestRepair() { return requestRepair; }
    public void setRequestRepair(RequestRepair requestRepair) { this.requestRepair = requestRepair; }
}