/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
