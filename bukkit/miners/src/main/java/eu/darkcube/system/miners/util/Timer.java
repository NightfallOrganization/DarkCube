package eu.darkcube.system.miners.util;

import eu.darkcube.system.miners.player.Message;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.miners.Miners;

import java.util.*;

public abstract class Timer {

    private BukkitRunnable runnable;

    private long startTime;
    private long endTime;
    private long originalEndTime;

    private final SortedSet<Integer> notifications = new TreeSet<>();
    private final Set<Integer> customNotifications = new HashSet<>();
    private int notificationInterval = 0;

    public abstract void onIncrement();

    public abstract void onEnd();

    public final boolean start(long millis) {
        if (runnable != null)
            return false;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + millis;
        this.originalEndTime = endTime;
        restoreNotifications();
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                onIncrement();
                sendNextNotification();
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
        restoreNotifications();
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

    /**
     * sets the interval in seconds at which players are notified.
     * value <1 will disable non-custom notifications
     */
    public final void setNotificationInterval(int secs) {
        notificationInterval = secs;
        restoreNotifications();
    }

    public final void addCustomNotification(int... secs) {
        for (int i : secs) {
            notifications.add(i);
            customNotifications.add(i);
        }
    }

    private void sendNextNotification() {
        int remainingSeconds = (int) getTimeRemainingMillis() / 1000;
        while (!notifications.isEmpty()) {
            if (notifications.last() > remainingSeconds)
                notifications.remove(notifications.last());
            else if (notifications.last() == remainingSeconds) {
                sendNotification(remainingSeconds);
                notifications.remove(notifications.last());
                return;
            } else
                return;
        }
    }

    public static void sendNotification(int secs) {
        if (secs > 60) {
            int mins = secs / 60;
            Bukkit.getOnlinePlayers().forEach(p -> Miners.sendTranslatedMessage(p, Message.REMAINING_MINUTES, mins));
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> Miners.sendTranslatedMessage(p, Message.REMAINING_SECONDS, secs));
        }
    }

    private void restoreNotifications() {
        notifications.clear();
        notifications.addAll(customNotifications);
        if (notificationInterval < 1)
            return;
        int remainingSeconds = (int) getTimeRemainingMillis() / 1000;
        for (int i = remainingSeconds / notificationInterval; i > 0; i--)
            notifications.add(i * notificationInterval);
    }

}
