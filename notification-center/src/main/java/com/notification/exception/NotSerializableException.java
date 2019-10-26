package com.notification.exception;

public class NotSerializableException extends RuntimeException {
    private static final long serialVersionUID = -2370931178164898699L;

    public NotSerializableException(final Class type) {
        super(String.format("class %s must implement java.io.Serializable", type.getName()));
    }
}
