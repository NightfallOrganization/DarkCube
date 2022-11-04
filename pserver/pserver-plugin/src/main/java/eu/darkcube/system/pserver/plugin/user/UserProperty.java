package eu.darkcube.system.pserver.plugin.user;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserProperty<T> {

	private final String name;
	private final Class<T> type;
	private final Gson gson;

	@Deprecated
	public UserProperty(String name) {
		this(name, null);
	}

	public UserProperty(String name, Class<T> type) {
		this(name, type, new Gson());
	}

	@SuppressWarnings("unchecked")
	public UserProperty(String name, Class<T> type, Gson gson) {
		this.name = name;
		if (type == null) {
			try {
				type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		this.type = type;
		this.gson = gson;
	}

	public UserProperty<T> remove(User user) {
		return this.set(user, null);
	}

	public UserProperty<T> set(User user, T value) {
		JsonObject extra = user.getExtra();
		if (value == null) {
			// Remove
			if (extra.has(name)) {
				extra.remove(name);
			}
		} else {
			// Update
			extra.add(name, gson.toJsonTree(value));
		}
		user.setExtra(extra);
		return this;
	}

	public UserProperty<T> save(User user) {
		user.saveExtra();
		return this;
	}

	public Optional<T> getSafe(User user) {
		JsonObject extra = user.getExtra();
		if (!extra.has(name)) {
			return Optional.empty();
		}
		JsonElement data = extra.get(name);
		if (data == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(gson.fromJson(data, type));
	}

	public T get(User user, T orElse) {
		return getSafe(user).orElse(orElse);
	}

	public Gson getGson() {
		return gson;
	}

	public String getName() {
		return name;
	}

	public Class<T> getType() {
		return type;
	}

	public T get(User user) {
		return getSafe(user).get();
	}
}
