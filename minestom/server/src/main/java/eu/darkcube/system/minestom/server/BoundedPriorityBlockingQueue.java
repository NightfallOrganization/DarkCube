/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedPriorityBlockingQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {
    private final ReentrantLock notEmptyLock = new ReentrantLock();
    private final Condition notEmpty = notEmptyLock.newCondition();
    private final ReentrantLock spaceAvailableLock = new ReentrantLock();
    private final Condition spaceAvailable = spaceAvailableLock.newCondition();
    private final int max;
    private final AtomicInteger size = new AtomicInteger();
    private final BlockingQueue<T> handle;

    public BoundedPriorityBlockingQueue(int max, BlockingQueue<T> handle) {
        this.max = max;
        this.handle = handle;
    }

    @Override public boolean offer(T t) {
        while (true) {
            int size = this.size.get();
            if (size == max) return false;
            if (this.size.compareAndSet(size, size + 1)) break;
        }
        boolean r = handle.offer(t);
        notEmptyLock.lock();
        notEmpty.signal();
        notEmptyLock.unlock();
        return r;
    }

    @Override public void put(T t) throws InterruptedException {
        spaceAvailableLock.lockInterruptibly();
        while (!offer(t)) spaceAvailable.await();
        spaceAvailableLock.unlock();
    }

    @Override public boolean offer(T t, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        if (offer(t)) return true;
        spaceAvailableLock.lockInterruptibly();
        try {
            while (true) {
                if (nanos <= 0) return false;
                if (offer(t)) return true;
                nanos = spaceAvailable.awaitNanos(nanos);
            }
        } finally {
            spaceAvailableLock.unlock();
        }
    }

    @Override public @NotNull T take() throws InterruptedException {
        final ReentrantLock lock = this.notEmptyLock;
        lock.lockInterruptibly();
        T result;
        try {
            while ((result = poll()) == null) notEmpty.await();
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override public T poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.notEmptyLock;
        lock.lockInterruptibly();
        try {
            while (true) {
                T result = poll();
                if (result != null) return result;
                if (nanos <= 0) return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override public int remainingCapacity() {
        return max - size.get();
    }

    @Override public int drainTo(Collection<? super T> c) {
        throw new UnsupportedOperationException();
    }

    @Override public int drainTo(Collection<? super T> c, int maxElements) {
        throw new UnsupportedOperationException();
    }

    @Override public T poll() {
        T res = handle.poll();
        if (res != null) size.decrementAndGet();
        return res;
    }

    @Override public T peek() {
        return handle.peek();
    }

    @Override public @NotNull Iterator<T> iterator() {
        return handle.iterator();
    }

    @Override public int size() {
        return size.get();
    }
}
