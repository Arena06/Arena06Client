package com.assemblr.oneshot.utils;


public final class MathUtils {
    
    private MathUtils() {}
    
    public static int clamp(int x, int min, int max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }
    
    public static double clamp(double x, double min, double max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }
    
}
