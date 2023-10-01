/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.util;

import java.util.concurrent.CompletableFuture;

public abstract class PriorityCallable<T> implements Runnable, Comparable<PriorityCallable<T>>, Prioritized {
    protected final int priority;
    protected final CompletableFuture<T> future;

    public PriorityCallable(int priority, CompletableFuture<T> future) {
        this.priority = priority;
        this.future = future;
    }

    @Override
    public void run() {
        try {
            if (future.isDone()) return;
            var t = call();
            complete(t);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void complete(T t) {
        future.complete(t);
    }

    @Override
    public int compareTo(PriorityCallable<T> o) {
        return Integer.compare(o.priority, priority);
    }

    public abstract T call();

    public CompletableFuture<T> future() {
        return future;
    }

    @Override
    public int priority() {
        return priority;
    }
}
