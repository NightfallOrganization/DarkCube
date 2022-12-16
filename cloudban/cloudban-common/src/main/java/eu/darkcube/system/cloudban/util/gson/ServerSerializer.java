/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import eu.darkcube.system.cloudban.util.ban.JsonSerializer;
import eu.darkcube.system.cloudban.util.ban.Server;

public class ServerSerializer extends JsonSerializer<Server> {
	@Override
	public JsonElement serialize(Server server, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(server.getServer());
	}

	@Override
	public Server deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return new Server(json.getAsString());
	}
}
