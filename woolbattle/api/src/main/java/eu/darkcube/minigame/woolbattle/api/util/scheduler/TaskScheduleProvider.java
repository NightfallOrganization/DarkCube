/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.scheduler;

import java.time.Duration;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface TaskScheduleProvider {
    @NotNull
    TaskSchedule duration(@NotNull Duration duration);

    @NotNull
    TaskSchedule tick(int tick);

    @NotNull
    TaskSchedule nextTick();

    @NotNull
    TaskSchedule immediate();

    @NotNull
    TaskSchedule seconds(int seconds);

    @NotNull
    TaskSchedule stop();
}
