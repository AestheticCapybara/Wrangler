package com.aesthetic.wrangler.controllers.helpers;

public class KeyAndValue {
    public Object key;
    public Object value;

    public KeyAndValue(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key.toString();
    }
}
