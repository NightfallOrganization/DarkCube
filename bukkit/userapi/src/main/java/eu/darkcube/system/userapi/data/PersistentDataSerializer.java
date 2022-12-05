package eu.darkcube.system.userapi.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

public class PersistentDataSerializer {

	public static <T> T deserialize(PersistentDataType<T> type, JsonDocument doc, String key) {
		return type.deserialize(doc, key);
	}

	public static <T> void serialize(PersistentDataType<T> type, JsonDocument doc, String key,
			T data) {
		type.serialize(doc, key, data);
	}

}
