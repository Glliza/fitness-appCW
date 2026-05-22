package com.fitnesscenter.app.entity;


import com.fitnesscenter.app.enums.EquipmentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @Column(name = "inventory_number", nullable = false, unique = true, length = 255)
    private String inventoryNumber;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;

    @Column(name = "buy_date")
    private LocalDate buyDate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @OneToMany(mappedBy = "equipment")
    private List<RequestRepair> repairRequests = new ArrayList<>();

    @OneToMany(mappedBy = "equipment")
    private List<Inventarization> inventarizations = new ArrayList<>();

    @OneToMany(mappedBy = "equipment")
    private List<RequestBuy> buyRequests = new ArrayList<>();

    @OneToOne(mappedBy = "equipment", cascade = CascadeType.ALL)
    private TORepair scheduledTO;

    public Equipment() {
        this.status = EquipmentStatus.ACTIVE;
        this.isDeleted = false;
    }

    // Геттеры и сеттеры
    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }

    public LocalDate getBuyDate() { return buyDate; }
    public void setBuyDate(LocalDate buyDate) { this.buyDate = buyDate; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }

    public List<RequestRepair> getRepairRequests() { return repairRequests; }
    public void setRepairRequests(List<RequestRepair> repairRequests) { this.repairRequests = repairRequests; }

    public List<Inventarization> getInventarizations() { return inventarizations; }
    public void setInventarizations(List<Inventarization> inventarizations) { this.inventarizations = inventarizations; }

    public List<RequestBuy> getBuyRequests() { return buyRequests; }
    public void setBuyRequests(List<RequestBuy> buyRequests) { this.buyRequests = buyRequests; }

    public TORepair getScheduledTO() { return scheduledTO; }
    public void setScheduledTO(TORepair scheduledTO) { this.scheduledTO = scheduledTO; }
}