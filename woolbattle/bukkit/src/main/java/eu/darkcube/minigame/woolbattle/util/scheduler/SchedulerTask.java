/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scheduler;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public final class SchedulerTask implements Comparable<SchedulerTask> {

    private long delay;
    private long repeat;
    private long lastExecution;
    private Integer weight;
    private Scheduler scheduler;

    SchedulerTask(@NotNull Scheduler scheduler, long delay) {
        this(scheduler, delay, 0);
    }

    SchedulerTask(@NotNull Scheduler scheduler, long delay, long repeat) {
        this.scheduler = scheduler;
        this.repeat = repeat;
        this.delay = delay;
        this.lastExecution = MinecraftServer.currentTick + delay - repeat;
        scheduler.woolbattle().schedulers().add(this);
    }

    SchedulerTask(@NotNull Scheduler scheduler, long delay, Integer weight) {
        this(scheduler, delay);
        weight(weight);
    }

    SchedulerTask(@NotNull Scheduler scheduler, long delay, long repeat, Integer weight) {
        this(scheduler, delay, repeat);
        weight(weight);
    }

    @Api
    public boolean repeating() {
        return repeat != 0;
    }

    @Api
    public long repeat() {
        return repeat;
    }

    @Api
    public long delay() {
        return delay;
    }

    @Api
    public int weight() {
        return weight;
    }

    public void weight(Integer weight) {
        this.weight = weight == null ? 0 : weight;
    }

    public void cancel() {
        if (scheduler == null) return;
        scheduler.woolbattle().schedulers().remove(this);
        scheduler = null;
        delay = 0;
        repeat = 0;
    }

    public void run() {
        lastExecution = MinecraftServer.currentTick;
        scheduler.run();
        if (!repeating()) {
            cancel();
        }
    }

    public boolean canExecute() {
        return scheduler != null && MinecraftServer.currentTick >= nextExecution();
    }

    public long lastExecution() {
        return lastExecution;
    }

    public long nextExecution() {
        if (repeat != 0) return lastExecution() + repeat;
        return lastExecution();
    }

    @Override
    public int compareTo(SchedulerTask o) {
        return weight.compareTo(o.weight);
    }
}
