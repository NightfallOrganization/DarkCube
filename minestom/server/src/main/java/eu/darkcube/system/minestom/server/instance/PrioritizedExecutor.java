/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.instance;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public interface PrioritizedExecutor {

    void submit(PriorityCallable<?> task);

    void remove(PriorityCallable<?> task);

    class Default implements PrioritizedExecutor {

        private final ThreadPoolExecutor executor;
        private final PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();

        public Default() {
            int maxSize = Runtime.getRuntime().availableProcessors();
            this.executor = new ThreadPoolExecutor(maxSize, maxSize, 1, TimeUnit.MINUTES, queue, new DefaultThreadFactory(), new DefaultRejectedExecutionHandler());
        }

        @Override public void submit(PriorityCallable<?> task) {
            executor.execute(task);
        }

        @Override public void remove(PriorityCallable<?> task) {
            queue.remove(task);
        }
    }

    class DefaultThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger();

        @Override public Thread newThread(@NotNull Runnable r) {
            var thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("ChunkGenerator-" + count.incrementAndGet());
            thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
            return thread;
        }
    }

    class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            new RejectedExecutionException("Failed to execute runnable " + r + " on executor " + executor).printStackTrace();
        }
    }
}
