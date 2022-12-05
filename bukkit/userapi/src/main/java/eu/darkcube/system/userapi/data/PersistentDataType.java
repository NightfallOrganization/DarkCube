package eu.darkcube.system.userapi.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

/**
 * @author DasBabyPixel
 * @param <T>
 */
public interface PersistentDataType<T> {

	T deserialize(JsonDocument doc, String key);

	void serialize(JsonDocument doc, String key, T data);

}
