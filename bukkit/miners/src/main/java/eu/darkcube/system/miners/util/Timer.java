package eu.darkcube.system.miners.util;

import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.miners.Miners;

public abstract class Timer {

    private BukkitRunnable runnable;

    private long startTime;
    private long endTime;
    private long originalEndTime;

    public abstract void onIncrement();

    public abstract void onEnd();

    public final boolean start(long millis) {
        if (runnable != null)
            return false;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + millis;
        this.originalEndTime = endTime;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                onIncrement();
                if (System.currentTimeMillis() > endTime) {
                    onEnd();
                    this.cancel();
                    runnable = null;
                }
            }
        };
        runnable.runTaskTimer(Miners.getInstance(), 0, 1);
        return true;
    }

    public final boolean setEndTime(long time) {
        if (runnable == null)
            return false;
        endTime = time;
        if (endTime > originalEndTime)
            originalEndTime = endTime;
        return true;
    }

    public final boolean cancel(boolean end) {
        if (runnable == null)
            return false;
        runnable.cancel();
        runnable = null;
        if (end)
            onEnd();
        return true;
    }

    public final long getStartTime() {
        return startTime;
    }

    public final long getEndTime() {
        return endTime;
    }

    public final long getOriginalEndTime() {
        return originalEndTime;
    }

    public final boolean isRunning() {
        return runnable != null;
    }

    public final long getTimeRemainingMillis() {
        return endTime - System.currentTimeMillis();
    }

}
