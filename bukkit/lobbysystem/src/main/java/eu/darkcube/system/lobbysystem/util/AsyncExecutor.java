package eu.darkcube.system.lobbysystem.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {

	private static ExecutorService service;

	public static void start() {
		service = Executors.newCachedThreadPool();
	}
	
	public static ExecutorService service() {
		return service;
	}

	public static void shutdown() {
		service.shutdown();
	}
}
