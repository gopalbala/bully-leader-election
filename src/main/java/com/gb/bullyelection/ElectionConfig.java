package com.gb.bullyelection;

import java.time.Duration;

public class ElectionConfig {
    public final Duration failureTimeout;
    public final Duration heartBeatFrequency;
    public final Duration failureDetectionFrequency;

    public ElectionConfig(Duration failureTimeout, Duration heartBeatFrequency, Duration failureDetectionFrequency) {
        this.failureTimeout = failureTimeout;
        this.heartBeatFrequency = heartBeatFrequency;
        this.failureDetectionFrequency = failureDetectionFrequency;
    }
}
