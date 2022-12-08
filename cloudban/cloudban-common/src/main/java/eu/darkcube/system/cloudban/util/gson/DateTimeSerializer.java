/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.gson;

import java.lang.reflect.Type;
import java.math.BigInteger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import eu.darkcube.system.cloudban.util.ban.DateTime;
import eu.darkcube.system.cloudban.util.ban.JsonSerializer;

public class DateTimeSerializer extends JsonSerializer<DateTime> {

	@Override
	public JsonElement serialize(DateTime dateTime, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(dateTime.getDurationInSeconds().toString());
	}

	@Override
	public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return new DateTime(new BigInteger(json.getAsString()));
	}
}
