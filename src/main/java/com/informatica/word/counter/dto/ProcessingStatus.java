/*
 * Copyright information here.
 */
package com.informatica.word.counter.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Processing Status Enum.
 */
public enum ProcessingStatus {

    IN_PROGRESS("IN_PROGRESS", "In Progress"),

    FAILURE("FAILURE", "Failure"),

    SUCCESS("SUCCESS", "Success");

    /** The value used when storing the enumeration in a datastore. */
    private String storageValue;

    /** The value used when displaying to user. */
    private String displayValue;

    ProcessingStatus(final String storageValue, final String displayValue) {

        this.storageValue = storageValue;
        this.displayValue = displayValue;

    }

    public String getStorageValue() {
        return storageValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    private static final Map<String, ProcessingStatus> STORAGE_VALUE_TO_MAP;
    static {
        Map<String, ProcessingStatus> m = new HashMap<>();
        Stream.of(ProcessingStatus.values()).forEach(t -> m.put(t.getStorageValue(), t));
        STORAGE_VALUE_TO_MAP = Collections.unmodifiableMap(m);
    }

    public static ProcessingStatus forStorageValue(final String val) {
        return STORAGE_VALUE_TO_MAP.get(val);
    }
}
