package com.geekhub.entities.enums;

public enum DocumentAttribute {
    PRIVATE, PUBLIC, FOR_FRIENDS;

    public static DocumentAttribute getValue(String name) {
        return valueOf(name.toUpperCase());
    }
}
