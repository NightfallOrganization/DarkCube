package eu.darkcube.minigame.woolbattle.nbt;

import java.util.HashMap;
import java.util.Map;

public class BasicDataStorage implements DataStorage {

	private final Map<String, Object> data = new HashMap<>();

	@Override
	public void set(String key, Object value) {
		data.put(key, value);
	}

	@Override
	public boolean has(String key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(String key) {
		return (T) data.remove(key);
	}
}
