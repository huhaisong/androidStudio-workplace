package com.example.loadbyengine.cardboard2.sensor;

public class SystemClock implements Clock {
    public SystemClock() {
    }
    public long nanoTime() {
        return System.nanoTime();
    }
}
