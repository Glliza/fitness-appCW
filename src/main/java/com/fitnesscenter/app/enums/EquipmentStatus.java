package com.fitnesscenter.app.enums;


public enum EquipmentStatus {
    ACTIVE("В эксплуатации"),
    BROKEN("Сломано"),
    ON_REPAIR("На ремонте"),
    WRITTEN_OFF("Списано");

    private final String rusName;

    EquipmentStatus(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }
}