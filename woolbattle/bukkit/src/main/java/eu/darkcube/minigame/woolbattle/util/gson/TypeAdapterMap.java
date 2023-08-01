/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.gson;

import java.lang.reflect.Type;

import org.bukkit.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.darkcube.minigame.woolbattle.map.DefaultMap;
import eu.darkcube.minigame.woolbattle.map.Map;

public class TypeAdapterMap
				implements JsonSerializer<Map>, JsonDeserializer<Map> {

	public static final TypeAdapterMap INSTANCE = new TypeAdapterMap();
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Location.class, TypeAdapterLocation.INSTANCE).create();

	private TypeAdapterMap() {
	}

	@Override
	public Map deserialize(JsonElement var1, Type var2,
					JsonDeserializationContext var3) throws JsonParseException {
		return GSON.fromJson(var1, DefaultMap.class);
	}

	@Override
	public JsonElement serialize(Map var1, Type var2,
					JsonSerializationContext var3) {
		return new JsonPrimitive(GSON.toJson(var1));
	}
}
