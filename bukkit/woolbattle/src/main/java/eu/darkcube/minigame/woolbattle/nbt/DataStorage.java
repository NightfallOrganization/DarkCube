package eu.darkcube.minigame.woolbattle.nbt;

public interface DataStorage {

	void set(String key, Object value);

	<T> T get(String key);

	boolean has(String key);

	<T> T remove(String key);

}
