package com.example.alertless.activities;

public enum Activities {
    MAIN_ACTIVITY (1),
    PROFILE_EDIT_ACTIVITY (2),
    SCHEDULER_ACTIVITY (3),
    APP_SELECTOR_ACTIVITY (4),
    NOTIFICATION_ACCESS_SETTINGS (5);

    Activities(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }
}
