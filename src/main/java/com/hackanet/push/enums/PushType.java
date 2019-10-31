package com.hackanet.push.enums;

public enum PushType {
    NEW_MESSAGE("NEW_MESSAGE");

    private String value;

    PushType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
