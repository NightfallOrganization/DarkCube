/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.scheduler;

import java.util.concurrent.CompletableFuture;

import dev.derklaro.aerogel.Singleton;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskScheduleProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Singleton
public final class TaskScheduleProviderImpl implements TaskScheduleProvider {
    static final TaskSchedule NEXT_TICK = new Tick(1);
    static final TaskSchedule STOP = new Stop();
    static final TaskSchedule IMMEDIATE = new Immediate();

    @Override
    public @NotNull TaskSchedule duration(@NotNull java.time.Duration duration) {
        return new Duration(duration);
    }

    @Override
    public @NotNull TaskSchedule tick(int tick) {
        return new Tick(tick);
    }

    @Override
    public @NotNull TaskSchedule nextTick() {
        return NEXT_TICK;
    }

    @Override
    public @NotNull TaskSchedule immediate() {
        return IMMEDIATE;
    }

    @Override
    public @NotNull TaskSchedule seconds(int seconds) {
        return new Second(seconds);
    }

    @Override
    public @NotNull TaskSchedule stop() {
        return STOP;
    }

    public record Future(@NotNull CompletableFuture<?> future) implements TaskSchedule {
    }

    public record Second(int second) implements TaskSchedule {
    }

    public record Duration(@NotNull java.time.Duration duration) implements TaskSchedule {
    }

    public record Tick(int tick) implements TaskSchedule {
    }

    public record Stop() implements TaskSchedule {
    }

    public record Immediate() implements TaskSchedule {
    }
}
