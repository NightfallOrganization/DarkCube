/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.server.util.ConcurrentLinkedDeque;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class ChunkPriorityQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {

    private final AtomicInteger tookSomething = new AtomicInteger(1);
    private final CopyOnWriteArrayList<Thread> waiting = new CopyOnWriteArrayList<>();
    private final ConcurrentLinkedDeque<T>[] queues;

    public ChunkPriorityQueue(int priorityCount) {
        this.queues = new ConcurrentLinkedDeque[priorityCount];
        for (int i = 0; i < this.queues.length; i++) {
            this.queues[i] = new ConcurrentLinkedDeque<>();
        }
    }

    public Entry<T> add(int priority, T chunk) {
        var node = queues[priority].offerLast(chunk);
        wake:
        if (tookSomething.compareAndSet(0, 1)) {
            Thread thread;
            do {
                var it = waiting.iterator();
                if (!it.hasNext()) break wake;
                thread = it.next();
            } while (!waiting.remove(thread));
            LockSupport.unpark(thread);
        }
        return new Entry<>(chunk, node);
    }

    @Override public @NotNull T take() throws InterruptedException {
        restart:
        while (true) {
            
            for (ConcurrentLinkedDeque<T> queue : queues) {
                T value = queue.pollFirst();
                if (value == null) continue;
                tookSomething.set(1);
                return value;
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

    @Override public boolean offer(@NotNull T t) {
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
