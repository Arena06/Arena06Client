package com.assemblr.arena06.client.utils;

public class DeltaRunner extends Thread {

    private final int fps;
    private final Runnable runnerNonDelta;
    private final DeltaRunnable updater;
    private volatile boolean running = false;
    private final int msPerCycle;
    private final Thread painter;

    public DeltaRunner(int fps, Runnable runner, DeltaRunnable updater) {
        this.fps = fps;
        this.runnerNonDelta = runner;
        this.updater = updater;
        this.msPerCycle = 1000 / this.fps;
        painter = new Thread(new Runnable() {
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                while (running) {
                    runnerNonDelta.run();
                    long elapsed = System.currentTimeMillis() - lastUpdate;
                    if (elapsed < msPerCycle) { // 60 FPS
                        try {
                            Thread.sleep(msPerCycle - elapsed);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    lastUpdate = System.currentTimeMillis();

                }
            }
        }
        );
    }

    @Override
    public void run() {
        running = true;
        painter.start();
        long lastUpdate = System.currentTimeMillis();
        while (running) {
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
