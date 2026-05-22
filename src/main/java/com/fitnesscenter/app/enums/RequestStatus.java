package com.fitnesscenter.app.enums;

public enum RequestStatus {
    OPENED("Открыта"),
    IN_WORK("В работе"),
    COMPLETED("Выполнена"),
    CANCELLED("Отменена");

    private final String rusName;

    RequestStatus(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }
}