package com.example.finanseapp.Enums;

public enum CategoryType {
    OPTION_ONE("Income", 0),
    OPTION_TWO("Expenses", 1);
    private final String displayName;
    private final int typeId;

    CategoryType(String displayName, int typeId) {
        this.displayName = displayName;
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
