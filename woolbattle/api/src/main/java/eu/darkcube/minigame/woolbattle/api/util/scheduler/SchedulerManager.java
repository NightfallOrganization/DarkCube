/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.scheduler;

import java.util.function.Supplier;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface SchedulerManager {
    @Api
    @NotNull SchedulerTask submit(@NotNull Supplier<TaskSchedule> task);

    @Api
    @NotNull SchedulerTask submit(@NotNull Supplier<TaskSchedule> task, @NotNull ExecutionType executionType);

    @Api
    @NotNull SchedulerTask schedule(@NotNull Runnable task, @NotNull TaskSchedule delay, @NotNull TaskSchedule repeat);

    @Api
    @NotNull SchedulerTask schedule(@NotNull Runnable task, @NotNull TaskSchedule delay, @NotNull TaskSchedule repeat, @NotNull ExecutionType executionType);
}
