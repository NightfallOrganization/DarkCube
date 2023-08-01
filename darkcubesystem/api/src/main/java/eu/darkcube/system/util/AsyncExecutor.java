/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncExecutor {

	private static ExecutorService service;

	public static void start() {
		service = Executors.newCachedThreadPool(new DefaultThreadFactory()); // 4 threads should be enough for all our
		// needs. This is for performance reasons and to stop creating new threads if we don't
		// have to
	}

	public static void stop() {
		service.shutdown();
	}

	public static ExecutorService service() {
		return service;
	}

	/**
	 * The default thread factory.
	 */
	private static class DefaultThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		DefaultThreadFactory() {
			group = Thread.currentThread().getThreadGroup();
			namePrefix = "AsyncExecutor-";
		}

		public Thread newThread(@NotNull Runnable r) {
			Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
}
