package com.example.sphere.cardboard2.sensor;

public class SystemClock implements Clock {
    public SystemClock() {
    }
    public long nanoTime() {
        return System.nanoTime();
    }
}
