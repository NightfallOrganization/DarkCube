/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

/**
 * @param <T> the data type
 *
 * @author DasBabyPixel
 */
public interface PersistentDataType<T> {

	T deserialize(JsonDocument doc, String key);

	void serialize(JsonDocument doc, String key, T data);

	T clone(T object);

}
