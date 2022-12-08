/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
