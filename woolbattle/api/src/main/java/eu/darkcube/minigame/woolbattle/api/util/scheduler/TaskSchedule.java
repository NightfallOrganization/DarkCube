/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.scheduler;

import static eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskScheduleImpl.provider;

import java.time.Duration;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface TaskSchedule {
    @Api
    static @NotNull TaskSchedule duration(@NotNull Duration duration) {
        return provider.duration(duration);
    }

    @Api
    static @NotNull TaskSchedule tick(int tick) {
        return provider.tick(tick);
    }

    @Api
    static @NotNull TaskSchedule nextTick() {
        return provider.nextTick();
    }

    @Api
    static @NotNull TaskSchedule immediate() {
        return provider.immediate();
    }

    @Api
    static @NotNull TaskSchedule seconds(int seconds) {
        return provider.seconds(seconds);
    }

    @Api
    static @NotNull TaskSchedule stop() {
        return provider.stop();
    }
}
