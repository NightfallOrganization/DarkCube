/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.server.util.ConcurrentLinkedDeque;
import eu.darkcube.system.minestom.server.util.Prioritized;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkPriorityQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {

    private final AtomicBoolean waiting = new AtomicBoolean(false);
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final ConcurrentLinkedDeque<T>[] queues;

    public ChunkPriorityQueue(int priorityCount) {
        this.queues = new ConcurrentLinkedDeque[priorityCount];
        for (int i = 0; i < this.queues.length; i++) {
            this.queues[i] = new ConcurrentLinkedDeque<>();
        }
    }

    public Entry<T> add(int priority, T chunk) {
        var node = queues[priority].offerLast(chunk);
        if (waiting.compareAndSet(true, false)) {
            try {
                lock.lock();
                notEmpty.signalAll();
            } finally {
                lock.unlock();
            }
        }
        return new Entry<>(chunk, node);
    }

    @Override public boolean offer(@NotNull T t) {
        if (t instanceof Prioritized p) {
            add(p.priority(), t);
            return true;
        }
        throw new UnsupportedOperationException();
    }

    @Override public @NotNull T take() {
        while (true) {
            for (var queue : queues) {
                var value = queue.pollFirst();
                if (value == null) continue;
                return value;
            }

            waiting.set(true);
            try {
                lock.lock();
                if (!waiting.get()) continue;
                notEmpty.awaitUninterruptibly();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override public @NotNull Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override public int size() {
        throw new UnsupportedOperationException();
    }

    @Override public void put(@NotNull T t) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override public boolean add(@NotNull T t) {
        return super.add(t);
    }

    @Override public boolean offer(T t, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override public T poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override public int remainingCapacity() {
        throw new UnsupportedOperationException();
    }

    @Override public int drainTo(@NotNull Collection<? super T> c) {
        throw new UnsupportedOperationException();
    }

    @Override public int drainTo(@NotNull Collection<? super T> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override public T poll() {
        throw new UnsupportedOperationException();
    }

    @Override public T peek() {
        throw new UnsupportedOperationException();
    }

    public record Entry<T>(T item, ConcurrentLinkedDeque.Node<?> node) {
        public boolean remove() {
            return node.unlink();
        }
    }
}
