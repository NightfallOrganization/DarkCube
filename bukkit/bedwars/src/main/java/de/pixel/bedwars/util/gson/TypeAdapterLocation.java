/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util.gson;

import java.lang.reflect.Type;

import org.bukkit.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.pixel.bedwars.util.Locations;

public class TypeAdapterLocation implements JsonSerializer<Location>, JsonDeserializer<Location> {

	public static final TypeAdapterLocation INSTANCE = new TypeAdapterLocation();
	
	private TypeAdapterLocation() {
	}
	
	@Override
	public Location deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3)
			throws JsonParseException {
		return Locations.deserialize(var1.getAsString(), null);
	}

	@Override
	public JsonElement serialize(Location var1, Type var2, JsonSerializationContext var3) {
		return new JsonPrimitive(Locations.serialize(var1));
	}
}
