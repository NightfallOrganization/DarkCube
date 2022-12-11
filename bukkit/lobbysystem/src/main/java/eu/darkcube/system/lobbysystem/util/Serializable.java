/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

public interface Serializable {
	
	default String serialize() {
		return GsonSerializer.gson.toJson(this);
	}
	
	default JsonDocument serializeToDocument() {
		return JsonDocument.newDocument(serialize());
	}
}
