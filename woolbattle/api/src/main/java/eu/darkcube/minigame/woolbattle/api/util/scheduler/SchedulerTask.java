/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.scheduler;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.function.Supplier;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface SchedulerTask {
    @Api
    @NotNull SchedulerManager schedulerManager();

    @Api
    void cancel();

    @Api
    boolean alive();

    @Api
    @NotNull ExecutionType executionType();

    @Api
    int id();

    @Api
    final class Builder {
        private final SchedulerManager scheduler;
        private final Runnable runnable;
        private ExecutionType executionType = ExecutionType.SYNC;
        private TaskSchedule delay = TaskSchedule.immediate();
        private TaskSchedule repeat = TaskSchedule.stop();

        public Builder(SchedulerManager scheduler, Runnable runnable) {
            this.scheduler = scheduler;
            this.runnable = runnable;
        }

        @Api
        public @NotNull Builder executionType(@NotNull ExecutionType executionType) {
            this.executionType = executionType;
            return this;
        }

        @Api
        public @NotNull Builder delay(@NotNull TaskSchedule schedule) {
            this.delay = schedule;
            return this;
        }

        @Api
        public @NotNull Builder repeat(@NotNull TaskSchedule schedule) {
            this.repeat = schedule;
            return this;
        }

        @Api
        public @NotNull SchedulerTask schedule() {
            var runnable = this.runnable;
            var delay = this.delay;
            var repeat = this.repeat;
            return scheduler.submit(new Supplier<>() {
                boolean first = true;

                @Override
                public TaskSchedule get() {
                    if (first) {
                        first = false;
                        return delay;
                    }
                    runnable.run();
                    return repeat;
                }
            }, executionType);
        }

        @Api
        public @NotNull Builder delay(@NotNull Duration duration) {
            return delay(TaskSchedule.duration(duration));
        }

        @Api
        public @NotNull Builder delay(long time, @NotNull TemporalUnit unit) {
            return delay(Duration.of(time, unit));
        }

        @Api
        public @NotNull Builder repeat(@NotNull Duration duration) {
            return repeat(TaskSchedule.duration(duration));
        }

        @Api
        public @NotNull Builder repeat(long time, @NotNull TemporalUnit unit) {
            return repeat(Duration.of(time, unit));
        }
    }
}
