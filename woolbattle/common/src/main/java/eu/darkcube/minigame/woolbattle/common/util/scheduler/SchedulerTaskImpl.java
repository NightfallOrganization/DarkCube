/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.scheduler;

import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.util.scheduler.ExecutionType;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class SchedulerTaskImpl implements SchedulerTask {
    private final @NotNull CommonSchedulerManager schedulerManager;
    private final int id;
    private final @NotNull ExecutionType executionType;
    private final @NotNull Supplier<TaskSchedule> task;
    private volatile boolean alive = true;

    public SchedulerTaskImpl(@NotNull CommonSchedulerManager schedulerManager, int id, @NotNull ExecutionType executionType, @NotNull Supplier<TaskSchedule> task) {
        this.schedulerManager = schedulerManager;
        this.id = id;
        this.executionType = executionType;
        this.task = task;
    }

    @Override
    public @NotNull CommonSchedulerManager schedulerManager() {
        return schedulerManager;
    }

    public Supplier<TaskSchedule> task() {
        return task;
    }

    @Override
    public void cancel() {
        alive = false;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean alive() {
        return alive;
    }

    @Override
    public @NotNull ExecutionType executionType() {
        return executionType;
    }
}
