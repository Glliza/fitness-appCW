package com.fitnesscenter.app.entity;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventarization")
public class Inventarization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_inventory_number", length = 255)
    private String equipmentInventoryNumber;

    private Integer count;

    @Column(name = "real_count")
    private Integer realCount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "equipment_inventory_number", referencedColumnName = "inventory_number", insertable = false, updatable = false)
    private Equipment equipment;

    public Inventarization() {
        this.date = LocalDate.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEquipmentInventoryNumber() { return equipmentInventoryNumber; }
    public void setEquipmentInventoryNumber(String equipmentInventoryNumber) { this.equipmentInventoryNumber = equipmentInventoryNumber; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public Integer getRealCount() { return realCount; }
    public void setRealCount(Integer realCount) { this.realCount = realCount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }
}