/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scheduler;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class Scheduler implements Runnable {

    private final WoolBattleBukkit woolbattle;
    private SchedulerTask task;
    private int weight;
    private Runnable run;

    public Scheduler(@NotNull WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        run = this;
    }

    public Scheduler(@NotNull WoolBattleBukkit woolbattle, @NotNull Runnable run) {
        this.woolbattle = woolbattle;
        this.run = run;
    }

    public void cancel() {
        if (isRunning()) {
            task.cancel();
            task = null;
        }
    }

    public void weight(int weight) {
        this.weight = weight;
        if (task != null) task.weight(weight);
    }

    public WoolBattleBukkit woolbattle() {
        return woolbattle;
    }

    public boolean isRunning() {
        return task != null;
    }

    public void runTask() {
        runTaskLater(0);
    }

    public void runTaskLater(long delay) {
        task = new SchedulerTask(this, delay, Integer.valueOf(weight));
    }

    public void runTaskTimer(long repeat) {
        runTaskTimer(0, repeat);
    }

    public void runTaskTimer(long delay, long repeat) {
        task = new SchedulerTask(this, delay, repeat, weight);
    }

    @Override public void run() {
        run.run();
    }

    public interface ConfiguredScheduler {
        void start();

        void stop();
    }
}
