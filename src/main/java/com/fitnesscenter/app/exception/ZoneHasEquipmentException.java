package com.fitnesscenter.app.exception;


public class ZoneHasEquipmentException extends RuntimeException {
    public ZoneHasEquipmentException(Long zoneId, String zoneName, Integer equipmentCount) {
        super("Zone '" + zoneName + "' has " + equipmentCount + " equipment. Delete them first.");
    }
}
