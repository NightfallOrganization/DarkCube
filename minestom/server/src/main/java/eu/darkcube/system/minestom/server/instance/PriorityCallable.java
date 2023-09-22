/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import eu.darkcube.system.minestom.server.util.Prioritized;

import java.util.concurrent.CompletableFuture;

public abstract class PriorityCallable<T> implements Runnable, Comparable<PriorityCallable<T>>, Prioritized {
    private final int priority;
    private final CompletableFuture<T> future;
    private volatile boolean cancel = false;

    public PriorityCallable(int priority, CompletableFuture<T> future) {
        this.priority = priority;
        this.future = future;
    }

    @Override public final void run() {
        if (cancel) return;
        if (future.isDone()) return;
        var t = call();
        synchronized (this) { // We need some point to synchronize the future on. This task is a good idea
            future.complete(t);
        }
    }

    void cancel() {
        cancel = true;
    }

    @Override public int compareTo(PriorityCallable<T> o) {
        return Integer.compare(o.priority, priority);
    }

    public abstract T call();

    public CompletableFuture<T> future() {
        return future;
    }

    @Override public int priority() {
        return priority;
    }
}
