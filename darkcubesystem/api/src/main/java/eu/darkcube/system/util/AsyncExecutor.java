/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {

	private static ExecutorService service;
	
	public static void start() {
		service = Executors.newCachedThreadPool();
	}
	
	public static void stop() {
		service.shutdown();
	}
	
	public static ExecutorService service() {
		return service;
	}
	
}
