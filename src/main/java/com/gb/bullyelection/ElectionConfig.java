package com.gb.bullyelection;

import lombok.Getter;

import java.time.Duration;
@Getter
public class ElectionConfig {
    private final Duration failureTimeout;
    private final Duration updateFrequency;
    public ElectionConfig(Duration failureTimeout, Duration updateFrequency) {
        this.failureTimeout = failureTimeout;
        this.updateFrequency = updateFrequency;
    }
}
