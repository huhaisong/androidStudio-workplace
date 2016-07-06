package com.example.a111.learn_sun.cardboard2.sensor;

public class SystemClock implements Clock {
    public SystemClock() {
    }
    public long nanoTime() {
        return System.nanoTime();
    }
}
