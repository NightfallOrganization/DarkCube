package eu.darkcube.system.userapi.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BasicMetaDataStorage implements MetaDataStorage {

	public final Map<Key, Object> data = new HashMap<>();

	@Override
	public void set(Key key, Object value) {
		data.put(key, value);
	}

	@Override
	public boolean has(Key key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key) {
		return (T) data.remove(key);
	}

	@Override
	public <T> T getOr(Key key, T orElse) {
		return has(key) ? get(key) : orElse;
	}

	@Override
	public <T> T getOr(Key key, Supplier<T> orElse) {
		return has(key) ? get(key) : orElse.get();
	}
}
