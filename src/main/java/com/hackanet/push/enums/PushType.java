package com.hackanet.push.enums;

public enum PushType {
    NEW_MESSAGE, HACKATHON_JOB_REVIEW_REQUEST, JOIN_TO_TEAM_REQUEST_STATUS;

    private String value;

    PushType() {
        this.value = name();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
