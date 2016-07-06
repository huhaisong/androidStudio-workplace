package com.example.mylibrary;

public class SystemClock implements Clock {
    public SystemClock() {
    }

    public long nanoTime() {
        return System.nanoTime();
    }
}
