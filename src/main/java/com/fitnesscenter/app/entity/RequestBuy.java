package com.fitnesscenter.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "request_buy")
public class RequestBuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_inventory_number", length = 255)
    private String equipmentInventoryNumber;

    private String name;

    private Integer count;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "equipment_inventory_number", referencedColumnName = "inventory_number", insertable = false, updatable = false)
    private Equipment equipment;

    public RequestBuy() {
        this.createdAt = LocalDate.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEquipmentInventoryNumber() { return equipmentInventoryNumber; }
    public void setEquipmentInventoryNumber(String equipmentInventoryNumber) { this.equipmentInventoryNumber = equipmentInventoryNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }
}