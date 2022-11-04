package eu.darkcube.minigame.woolbattle.util;

public interface Serializable {
	default String serialize() {
		return GsonSerializer.gson.toJson(this);
	}
}