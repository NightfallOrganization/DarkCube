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
