package com.assemblr.arena06.client.utils;


public interface DeltaRunnable {
    
    public static final DeltaRunnable NULL = new DeltaRunnable() {
        public void run(double delta) {}
    };
    
    public void run(double delta);
    
}
