package eu.darkcube.system.pserver.plugin.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SingleInstance {

	private static final ConcurrentMap<Class<? extends SingleInstance>, SingleInstance> instances = new ConcurrentHashMap<>();

	public SingleInstance() {
		final Class<? extends SingleInstance> clazz = getClass();
		if (instances.containsKey(clazz)) {
			throw new RuntimeException("May not create an instance twice!");
		}
		instances.put(clazz, this);
	}

	@SuppressWarnings("unchecked")
	public static <T extends SingleInstance> T getInstance(Class<T> clazz) {
		return (T) instances.getOrDefault(clazz, null);
	}
}
