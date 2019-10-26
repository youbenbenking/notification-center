package com.notification.common.result;

public class NameValuePair<V, N> {
    private V value;
    private N name;

    public NameValuePair() {}

    public NameValuePair(V value, N name) {
        this.value = value;
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public N getName() {
        return name;
    }

    public void setName(N name) {
        this.name = name;
    }
}
