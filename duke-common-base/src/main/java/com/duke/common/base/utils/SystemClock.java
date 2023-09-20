package com.duke.common.base.utils;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SystemClock {
    private final long period;
    private volatile long now;

    public SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        this.scheduleClockUpdating();
    }

    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor((runnable) -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
//        scheduler.scheduleAtFixedRate(() -> {
//            this.now = System.currentTimeMillis();
//        }, this.period, this.period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return this.now;
    }

    private static SystemClock instance() {
        return SystemClock.InstanceHolder.INSTANCE;
    }

    public static long now() {
        return instance().currentTimeMillis();
    }

    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1L);

        private InstanceHolder() {
        }
    }
}