package eu.darkcube.minigame.woolbattle.util;

import java.util.HashMap;
import java.util.Map;

public interface Enableable {

	Map<Enableable, Boolean> enabled = new HashMap<>();

	default void enable() {
		if (!isEnabled()) {
			enabled.put(this, true);
			onEnable();
		}
	}

	default void disable() {
		if (isEnabled()) {
			enabled.put(this, false);
			onDisable();
		}
	}

	default boolean isEnabled() {
		return enabled.get(this) == null ? false : enabled.get(this);
	}

	void onEnable();

	void onDisable();

}
