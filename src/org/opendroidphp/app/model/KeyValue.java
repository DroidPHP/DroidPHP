package org.opendroidphp.app.model;

import java.util.HashMap;

public class KeyValue {
    private HashMap<String, String> keyValue;

    public KeyValue(final HashMap<String, String> keyValue) {
        this.keyValue = keyValue;
    }

    public String findValue(final String keyName) {
        return keyValue.get(keyName);
    }

    public boolean hasKey(final String keyName) {
        return keyValue.containsKey(keyName);
    }
}
