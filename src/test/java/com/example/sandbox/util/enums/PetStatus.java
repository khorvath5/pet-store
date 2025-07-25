package com.example.sandbox.util.enums;

public enum PetStatus {
    AVAILABLE("available"),
    PENDING("pending"),
    SOLD("sold");

    private final String value;

    PetStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String status) {
        for (PetStatus s : values()) {
            if (s.value.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
}
