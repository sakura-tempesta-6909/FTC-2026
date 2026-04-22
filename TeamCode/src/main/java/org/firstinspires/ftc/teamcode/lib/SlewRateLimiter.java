package org.firstinspires.ftc.teamcode.lib;

import java.util.function.Supplier;

public class SlewRateLimiter implements Supplier<Double> {
    private final Supplier<Double> input;
    private final double rateLimitPerSec;
    private double value;
    private long lastNanos = -1L;

    public SlewRateLimiter(Supplier<Double> input, double rateLimitPerSec) {
        this.input = input;
        this.rateLimitPerSec = rateLimitPerSec;
    }

    @Override
    public Double get() {
        double target = input.get();
        long now = System.nanoTime();
        if (lastNanos < 0L) {
            lastNanos = now;
            value = target;
            return value;
        }
        double dt = (now - lastNanos) / 1e9;
        lastNanos = now;
        double maxStep = rateLimitPerSec * dt;
        double delta = target - value;
        if (delta > maxStep) delta = maxStep;
        else if (delta < -maxStep) delta = -maxStep;
        value += delta;
        return value;
    }
}
