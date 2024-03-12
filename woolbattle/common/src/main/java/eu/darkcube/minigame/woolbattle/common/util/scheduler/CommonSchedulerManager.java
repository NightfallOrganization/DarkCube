/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.util.scheduler.ExecutionType;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerManager;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import org.jctools.queues.MpmcUnboundedXaddArrayQueue;

public class CommonSchedulerManager implements SchedulerManager {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        var thread = new Thread(r);
        thread.setName("CommonSchedulerManagerExecutor");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicInteger taskCounter = new AtomicInteger(0);
    private final MpmcUnboundedXaddArrayQueue<SchedulerTaskImpl> taskQueue = new MpmcUnboundedXaddArrayQueue<>(64);
    private final Int2ObjectAVLTreeMap<List<SchedulerTaskImpl>> tickTaskQueue = new Int2ObjectAVLTreeMap<>();
    private int tickState;

    public void process() {
        processTick(0);
    }

    public void processTick() {
        processTick(1);
    }

    private void processTick(int tickDelta) {
        synchronized (tickTaskQueue) {
            this.tickState += tickDelta;
            while (!tickTaskQueue.isEmpty()) {
                var tickToProcess = tickTaskQueue.firstIntKey();
                if (tickToProcess > tickState) break;
                var tasks = tickTaskQueue.remove(tickToProcess);
                tasks.forEach(taskQueue::relaxedOffer);
            }
        }
        if (!taskQueue.isEmpty()) {
            taskQueue.drain(task -> {
                if (!task.alive()) return;
                switch (task.executionType()) {
                    case SYNC -> execute(task);
                    case ASYNC -> scheduler.execute(() -> execute(task));
                    default -> throw new IllegalStateException("Unexpected value: " + task.executionType());
                }
            });
        }
    }

    @Override
    public @NotNull SchedulerTask submit(@NotNull Supplier<TaskSchedule> task) {
        return submit(task, ExecutionType.SYNC);
    }

    @Override
    public @NotNull SchedulerTask submit(@NotNull Supplier<TaskSchedule> task, @NotNull ExecutionType executionType) {
        final var schedulerTask = new SchedulerTaskImpl(this, taskCounter.getAndIncrement(), executionType, task);
        execute(schedulerTask);
        return schedulerTask;
    }

    @Override
    public @NotNull SchedulerTask schedule(@NotNull Runnable task, @NotNull TaskSchedule delay, @NotNull TaskSchedule repeat) {
        return schedule(task, delay, repeat, ExecutionType.SYNC);
    }

    @Override
    public @NotNull SchedulerTask schedule(@NotNull Runnable task, @NotNull TaskSchedule delay, @NotNull TaskSchedule repeat, @NotNull ExecutionType executionType) {
        return new SchedulerTask.Builder(this, task).delay(delay).repeat(repeat).executionType(executionType).schedule();
    }

    private void safeExecute(SchedulerTaskImpl task) {
        switch (task.executionType()) {
            case SYNC -> taskQueue.offer(task);
            case ASYNC -> scheduler.execute(() -> {
                if (!task.alive()) return;
                execute(task);
            });
            default -> throw new IllegalStateException("Unexpected value: " + task.executionType());
        }
    }

    private void execute(SchedulerTaskImpl task) {
        var schedule = task.task().get();
        switch (schedule) {
            case TaskScheduleProviderImpl.Second(var second) -> scheduler.schedule(() -> safeExecute(task), second, TimeUnit.SECONDS);
            case TaskScheduleProviderImpl.Duration(var duration) -> scheduler.schedule(() -> safeExecute(task), duration.toMillis(), TimeUnit.MILLISECONDS);
            case TaskScheduleProviderImpl.Tick(var tick) -> {
                synchronized (tickTaskQueue) {
                    final var target = tickState + tick;
                    tickTaskQueue.computeIfAbsent(target, i -> new ArrayList<>(1)).add(task);
                }
            }
            case TaskScheduleProviderImpl.Future(var future) -> future.thenRun(() -> safeExecute(task));
            case TaskScheduleProviderImpl.Stop() -> task.cancel();
            case TaskScheduleProviderImpl.Immediate() -> taskQueue.offer(task);
            default -> throw new IllegalStateException("Invalid Schedule: " + schedule);
        }
    }
}
