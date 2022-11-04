package eu.darkcube.system.commons;

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
