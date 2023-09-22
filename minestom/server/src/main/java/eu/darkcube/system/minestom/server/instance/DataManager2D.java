/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import space.vectrix.flare.fastutil.Long2ObjectSyncMap;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataManager2D<T> {

    private final Lock writeLock = new ReentrantLock();
    private final Long2ObjectSyncMap<T> cache = Long2ObjectSyncMap.hashmap();
    private final Long2ObjectSyncMap<GeneratorTask<T>> loading = Long2ObjectSyncMap.hashmap();
    private final PrioritizedExecutor executor;
    private final Long2ObjectFunction<T> dataGenerator;
    private final PriorityCalculator priorityCalculator;
    private final Callback<? super T> loadCallback;
    private final Callback<? super T> unloadCallback;

    public DataManager2D(Long2ObjectFunction<T> loader, PriorityCalculator priorityCalculator, Callback<? super T> loadCallback, Callback<? super T> unloadCallback) {
        this(new PrioritizedExecutor.Default(32), loader, priorityCalculator, loadCallback, unloadCallback);
    }

    public DataManager2D(PrioritizedExecutor executor, Long2ObjectFunction<T> loader, PriorityCalculator priorityCalculator, Callback<? super T> loadCallback, Callback<? super T> unloadCallback) {
        this.executor = executor;
        this.dataGenerator = loader;
        this.priorityCalculator = priorityCalculator;
        this.loadCallback = loadCallback == null ? (Callback<? super T>) Callback.EMPTY : loadCallback;
        this.unloadCallback = unloadCallback == null ? (Callback<? super T>) Callback.EMPTY : unloadCallback;
    }

    public static long combine(int x, int y) {
        return (long) x << 32 | (y & 0xFFFFFFFFL);
    }

    public static int splitX(long index) {
        return (int) (index >> 32);
    }

    public static int splitY(long index) {
        return (int) index;
    }

    private static void bresenheim(int centerX, int centerY, float radius, BresenheimConsumer consumer) {
        float t1 = radius / 16;
        int x = (int) radius;
        int y = 0;
        while (!(x < y)) {
            consumer.color(centerX, centerY, x, y);
            y++;
            t1 += y;
            float t2 = t1 - x;
            if (t2 >= 0) {
                t1 = t2;
                if (y < x) consumer.color(centerX, centerY, y - 1, x);
                x--;
            }
        }
    }

    private static void bresenheimAll(int centerX, int centerY, float radius, BresenheimConsumer consumer) {
        bresenheim(centerX, centerY, radius, (centerX1, centerY1, relativeX, relativeY) -> {
            bresenheimAllLoadRect(centerX1, centerY1, -relativeX, relativeY, relativeX, relativeY, consumer);
            if (relativeY != 0) bresenheimAllLoadRect(centerX1, centerY1, -relativeX, -relativeY, relativeX, -relativeY, consumer);
        });
    }

    private static void bresenheimAllLoadRect(int centerX, int centerY, int x1, int y1, int x2, int y2, BresenheimConsumer consumer) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                consumer.color(centerX, centerY, x, y);
            }
        }
    }

    public int cacheSize() {
        return cache.size();
    }

    public void unload(int x, int y) {
        var key = combine(x, y);
        T removed;
        writeLock.lock();
        try {
            var cached = cache.remove(key);
            if (cached == null) {
                var task = loading.remove(key);
                if (task == null) return; // The chunk was not loaded and not being loaded
                if (task.requireCount.decrementAndGet() != 0) {
                    return; // Someone else still needs this
                }
                task.cancel();
                writeLock.unlock();
                try {
                    synchronized (task) {
                        if (task.future().cancel(true)) return; // We stopped the loading process
                        if (task.future().isCompletedExceptionally()) return; // Another invocation stopped the future

                        System.out.println(task.future().isDone() + " " + task.future().isCompletedExceptionally() + " " + task
                                .future()
                                .isCancelled());
                    }
                } finally {
                    writeLock.lock();
                }
                throw new ConcurrentModificationException("Something fishy going on here");
            }
            removed = cached;
        } finally {
            writeLock.unlock();
        }
        unloadCallback.handle(x, y, removed);
    }

    public void loadAround(int x, int y, float range) {
        bresenheim(x, y, range, (centerX, centerY, relativeX, relativeY) -> {
            loadRect(centerX, centerY, -relativeX, relativeY, relativeX, relativeY);
            if (relativeY != 0) loadRect(centerX, centerY, -relativeX, -relativeY, relativeX, -relativeY);
        });
    }

    public void unloadAround(int x, int y, float range) {
        bresenheim(x, y, range, (centerX, centerY, relativeX, relativeY) -> {
            unloadRect(centerX, centerY, -relativeX, relativeY, relativeX, relativeY);
            if (relativeY != 0) unloadRect(centerX, centerY, -relativeX, -relativeY, relativeX, -relativeY);
        });
    }

    public void move(int oldX, int oldY, int newX, int newY, float oldRadius, float newRadius) {
        long time1 = System.nanoTime();
        LongCollection toUnload = new LongArrayList();
        LongCollection toLoad = new LongOpenHashSet();
        LongCollection rePrioritize = new LongArrayList();
        bresenheimAll(newX, newY, newRadius, (centerX, centerY, relativeX, relativeY) -> {
            var key = combine(centerX + relativeX, centerY + relativeY);
            toLoad.add(key);
        });
        bresenheimAll(oldX, oldY, oldRadius, (centerX, centerY, relativeX, relativeY) -> {
            var key = combine(centerX + relativeX, centerY + relativeY);
//            if (toLoad.rem(key)) return;
            if (toLoad.rem(key)) {
                rePrioritize.add(key);
                return;
            }
            toUnload.add(key);
        });
//        // Just make sure near chunks are loaded FAST
//        bresenheimAll(newX, newY, newRadius / 4, (centerX, centerY, relativeX, relativeY) -> {
//            var key = combine(centerX + relativeX, centerY + relativeY);
//            toLoad.add(key);
//        });
        var it = toUnload.iterator();
        for (long key; it.hasNext() && (key = it.nextLong()) == key; ) {
            unload(splitX(key), splitY(key));
        }
        it = toLoad.iterator();
        for (long key; it.hasNext() && (key = it.nextLong()) == key; ) {
            var x = splitX(key);
            var y = splitY(key);
            load(x, y, priorityCalculator.priority(newX, newY, x, y));
        }
        it = rePrioritize.iterator();
        for (long key; it.hasNext() && (key = it.nextLong()) == key; ) {
            var x = splitX(key);
            var y = splitY(key);
            tryRePrioritizeIfSingle(x, y, priorityCalculator.priority(newX, newY, x, y));
        }
        System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time1));
    }

    public void loadRect(int centerX, int centerY, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                int realX = centerX + x;
                int realY = centerY + y;
                load(realX, realY, priorityCalculator.priority(centerX, centerY, realX, realY));
            }
        }
    }

    public void unloadRect(int centerX, int centerY, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                int realX = centerX + x;
                int realY = centerY + y;
                unload(realX, realY);
            }
        }
    }

    private void tryRePrioritizeIfSingle(int x, int y, int priority) {
        var key = combine(x, y);
        writeLock.lock();
        try {
            var cached = cache.get(key);
            if (cached != null) return; // No need
            var task = loading.get(key);
            if (task == null) {
                System.out.println("What the heck task is null");
                return;
            }
            if (task.requireCount.get() == 1) {
                task.cancel();
                var newTask = new GeneratorTask<>(priority, task.future(), dataGenerator, task.lateFuture, key);
                loading.put(key, newTask);
                executor.submit(newTask);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public CompletableFuture<T> load(int x, int y, int priority) {
        var key = combine(x, y);
        GeneratorTask<T> task;
        CompletableFuture<T> future;
        writeLock.lock();
        try {
            var cached = cache.get(key);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }
            if (loading.containsKey(key)) {
                task = loading.get(key);
                // We might need to re-prioritize the task
                if (task.priority() < priority) {
                    task.cancel();
                    var newTask = new GeneratorTask<>(priority, task.future(), dataGenerator, task.lateFuture, key);
                    newTask.requireCount.set(task.requireCount.get() + 1);
                    loading.put(key, newTask);
                    executor.submit(newTask);
                }
                return task.future();
            }
            var originalFuture = new CompletableFuture<T>();
            future = originalFuture.thenApply(t -> {
                writeLock.lock();
                var prev = cache.putIfAbsent(key, t);
                loading.remove(key);
                writeLock.unlock();
                if (prev != null)
                    throw new ConcurrentModificationException("Someone else loaded the chunk while we were loading it. Should be impossible.");
                loadCallback.handle(x, y, t);
                return t;
            });
            future.exceptionally(throwable -> {
                writeLock.lock();
                loading.remove(key);
                writeLock.unlock();
                if (throwable instanceof CompletionException e && e.getCause() != null && e.getCause() instanceof CancellationException) {
                    return null;
                }
                throwable.printStackTrace();
                return null;
            });
            var gen = new GeneratorTask<>(priority, originalFuture, dataGenerator, future, key);
            task = gen;
            loading.put(key, gen);
        } finally {
            writeLock.unlock();
        }

        executor.submit(task);
        return future;
    }

    public @Nullable T getCached(int x, int y) {
        return cache.get(combine(x, y));
    }

    public interface Callback<T> {
        Callback<?> EMPTY = (x, y, data) -> {
        };

        void handle(int x, int y, T data);
    }

    private interface BresenheimConsumer {
        void color(int centerX, int centerY, int relativeX, int relativeY);
    }

    public interface PriorityCalculator {
        int priority(int centerX, int centerY, int x, int y);
    }

    private static class GeneratorTask<T> extends PriorityCallable<T> {
        private final Long2ObjectFunction<T> generator;
        private final CompletableFuture<T> lateFuture;
        private final AtomicInteger requireCount = new AtomicInteger(1);
        private final long key;

        public GeneratorTask(int priority, CompletableFuture<T> future, Long2ObjectFunction<T> generator, CompletableFuture<T> lateFuture, long key) {
            super(priority, future);
            this.generator = generator;
            this.lateFuture = lateFuture;
            this.key = key;
        }

        @Override public T call() {
            return generator.apply(key);
        }

    }
}
