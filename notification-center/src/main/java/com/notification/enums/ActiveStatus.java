package com.notification.enums;

import java.util.Arrays;
import java.util.Objects;

public enum ActiveStatus {

    ACTIVE("有效", 1),
    INACTIVE("无效", 0);

    private final String name;
    private final int value;

    ActiveStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() { return value; }

    public static ActiveStatus getByValue(int value) {
        return Arrays.stream(ActiveStatus.values())
            .filter(c -> Objects.equals(c.getValue(), value))
            .findFirst()
            .orElse(null);
    }
}
