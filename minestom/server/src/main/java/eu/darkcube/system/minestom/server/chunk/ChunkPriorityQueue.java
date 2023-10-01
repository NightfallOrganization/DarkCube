/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.chunk;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.server.util.ConcurrentLinkedNodeDeque;
import eu.darkcube.system.minestom.server.util.Prioritized;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChunkPriorityQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {

    private final AtomicBoolean waiting = new AtomicBoolean(false);
    private final ConcurrentLinkedNodeDeque<T>[] queues;

    public ChunkPriorityQueue(int priorityCount) {
        this.queues = new ConcurrentLinkedNodeDeque[priorityCount];
        for (int i = 0; i < this.queues.length; i++) {
            this.queues[i] = new ConcurrentLinkedNodeDeque<>();
        }
    }

    public Entry<T> add(int priority, T chunk) {
        var node = queues[priority].offerLast(chunk);
        if (waiting.compareAndSet(true, false)) {
            synchronized (this) {
                notifyAll();
            }
        }
        return new Entry<>(chunk, node);
    }

    @Override
    public boolean offer(@NotNull T t) {
        if (t instanceof Prioritized p) {
            add(p.priority(), t);
            return true;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull T take() throws InterruptedException {
        while (true) {
            for (var queue : queues) {
                var value = queue.pollFirst();
                if (value == null) continue;
                return value;
            }

            waiting.set(true);
            synchronized (this) {
                if (!waiting.get()) continue;
                this.wait();
            }
        }
    }

    @Override
    public int drainTo(@NotNull Collection<? super T> c) {
        int count = 0;
        for (ConcurrentLinkedNodeDeque<T> queue : queues) {
            T entry;
            while ((entry = queue.pollFirst()) != null) {
                c.add(entry);
                count++;
            }
        }
        return count;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        for (ConcurrentLinkedNodeDeque<T> queue : queues) {
            if (queue.peekFirst() != null) return false;
        }
        return true;
    }

    @Override
    public int size() {
        return -1;
    }

    @Override
    public void put(@NotNull T t) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(@NotNull T t) {
        return super.add(t);
    }

    @Override
    public boolean offer(T t, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remainingCapacity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int drainTo(@NotNull Collection<? super T> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T poll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T peek() {
        throw new UnsupportedOperationException();
    }

    public record Entry<T>(T item, ConcurrentLinkedNodeDeque.Node<?> node) {
        public boolean remove() {
            return node.unlink();
        }
    }
}
