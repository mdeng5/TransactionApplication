package dev.codescreen.model;

public enum ResponseCode {
    APPROVED,
    DENIED;
    public static ResponseCode fromString(String value) {
        return ResponseCode.valueOf(value.toUpperCase());
    }
}
