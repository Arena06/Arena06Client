package com.assemblr.arena06.client.utils;


public class DeltaRunner extends Thread {
    
    private final int fps;
    private final Runnable runner;
    private final DeltaRunnable updater;
    private volatile boolean running = false;
    
    public DeltaRunner(int fps, Runnable runner, DeltaRunnable updater) {
        this.fps = fps;
        this.runner = runner;
        this.updater = updater;
    }
    
    @Override
    public void run() {
        running = true;
        int msPerCycle = 1000 / fps;
        long lastUpdate = System.currentTimeMillis();
        while (running) {
            runner.run();
            
            long elapsed = System.currentTimeMillis() - lastUpdate;
            if (elapsed < msPerCycle) { // 60 FPS
                try {
                    Thread.sleep(msPerCycle - elapsed);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            
            long now = System.currentTimeMillis();
            updater.run((now - lastUpdate) / 1000.0);
            lastUpdate = now;
        }
    }
    
    public void requestStop() {
        running = false;
    }
    
}
