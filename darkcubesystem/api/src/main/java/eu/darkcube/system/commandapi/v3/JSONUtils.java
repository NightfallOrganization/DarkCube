/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.com.google.gson.*;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import org.apache.commons.lang.text.StrBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JSONUtils {
	private static final Gson GSON = (new GsonBuilder()).create();

	public static boolean isString(JsonObject json, String memberName) {
		return !JSONUtils.isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isString();
	}

	public static boolean isString(JsonElement json) {
		return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isString();
	}

	public static boolean isNumber(JsonElement json) {
		return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isNumber();
	}

	public static boolean isBoolean(JsonObject json, String memberName) {
		return !JSONUtils.isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isBoolean();
	}

	public static boolean isJsonArray(JsonObject json, String memberName) {
		return !JSONUtils.hasField(json, memberName) ? false : json.get(memberName).isJsonArray();
	}

	public static boolean isJsonPrimitive(JsonObject json, String memberName) {
		return !JSONUtils.hasField(json, memberName) ? false : json.get(memberName).isJsonPrimitive();
	}

	public static boolean hasField(JsonObject json, String memberName) {
		if (json == null) {
			return false;
		}
		return json.get(memberName) != null;
	}

	public static String getString(JsonElement json, String memberName) {
		if (json.isJsonPrimitive()) {
			return json.getAsString();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + JSONUtils.toString(json));
	}

	public static String getString(JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getString(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
	}

	public static String getString(JsonObject json, String memberName, String fallback) {
		return json.has(memberName) ? JSONUtils.getString(json.get(memberName), memberName) : fallback;
	}

	public static boolean getBoolean(JsonElement json, String memberName) {
		if (json.isJsonPrimitive()) {
			return json.getAsBoolean();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a Boolean, was " + JSONUtils.toString(json));
	}

	public static boolean getBoolean(JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getBoolean(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
	}

	public static boolean getBoolean(JsonObject json, String memberName, boolean fallback) {
		return json.has(memberName) ? JSONUtils.getBoolean(json.get(memberName), memberName) : fallback;
	}

	public static float getFloat(JsonElement json, String memberName) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsFloat();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a Float, was " + JSONUtils.toString(json));
	}

	public static float getFloat(JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getFloat(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Float");
	}

	public static float getFloat(JsonObject json, String memberName, float fallback) {
		return json.has(memberName) ? JSONUtils.getFloat(json.get(memberName), memberName) : fallback;
	}

	public static long getLong(JsonElement json, String memberName) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsLong();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a Long, was " + JSONUtils.toString(json));
	}

	public static long getLong(JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getLong(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Long");
	}

	public static long getLong(JsonObject json, String memberName, long fallback) {
		return json.has(memberName) ? JSONUtils.getLong(json.get(memberName), memberName) : fallback;
	}

	public static int getInt(JsonElement json, String memberName) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsInt();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + JSONUtils.toString(json));
	}

	public static int getInt(JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getInt(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
	}

	public static int getInt(JsonObject json, String memberName, int fallback) {
		return json.has(memberName) ? JSONUtils.getInt(json.get(memberName), memberName) : fallback;
	}

	public static byte getByte(JsonElement json, String memberName) {
		if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsByte();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a Byte, was " + JSONUtils.toString(json));
	}

	public static byte getByte(JsonObject json, String memberName, byte fallback) {
		return json.has(memberName) ? JSONUtils.getByte(json.get(memberName), memberName) : fallback;
	}

	public static JsonObject getJsonObject(
			JsonElement json, String memberName) {
		if (json.isJsonObject()) {
			return json.getAsJsonObject();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + JSONUtils.toString(json));
	}

	public static JsonObject getJsonObject(
			JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getJsonObject(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject");
	}

	public static JsonObject getJsonObject(
			JsonObject json, String memberName, JsonObject fallback) {
		return json.has(memberName) ? JSONUtils.getJsonObject(json.get(memberName), memberName) : fallback;
	}

	public static JsonArray getJsonArray(
			JsonElement json, String memberName) {
		if (json.isJsonArray()) {
			return json.getAsJsonArray();
		}
		throw new JsonSyntaxException("Expected " + memberName + " to be a JsonArray, was " + JSONUtils.toString(json));
	}

	public static JsonArray getJsonArray(
			JsonObject json, String memberName) {
		if (json.has(memberName)) {
			return JSONUtils.getJsonArray(json.get(memberName), memberName);
		}
		throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonArray");
	}

	public static JsonArray getJsonArray(
			JsonObject json, String memberName, JsonArray fallback) {
		return json.has(memberName) ? JSONUtils.getJsonArray(json.get(memberName), memberName) : fallback;
	}

	public static <T> T deserializeClass(JsonElement json, String memberName, JsonDeserializationContext context,
			Class<? extends T> adapter) {
		if (json != null) {
			return context.deserialize(json, adapter);
		}
		throw new JsonSyntaxException("Missing " + memberName);
	}

	public static <T> T deserializeClass(JsonObject json, String memberName, JsonDeserializationContext context,
			Class<? extends T> adapter) {
		if (json.has(memberName)) {
			return JSONUtils.deserializeClass(json.get(memberName), memberName, context, adapter);
		}
		throw new JsonSyntaxException("Missing " + memberName);
	}

	public static <T> T deserializeClass(JsonObject json, String memberName, T fallback,
			JsonDeserializationContext context, Class<? extends T> adapter) {
		return json.has(memberName) ? JSONUtils.deserializeClass(json.get(memberName), memberName, context, adapter) : fallback;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static String toString(JsonElement json) {
		String s = JSONUtils.abbreviateMiddle(String.valueOf(json), "...", 10);
		if (json == null) {
			return "null (missing)";
		} else if (json.isJsonNull()) {
			return "null (json)";
		} else if (json.isJsonArray()) {
			return "an array (" + s + ")";
		} else if (json.isJsonObject()) {
			return "an object (" + s + ")";
		} else {
			if (json.isJsonPrimitive()) {
				JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();
				if (jsonprimitive.isNumber()) {
					return "a number (" + s + ")";
				}

				if (jsonprimitive.isBoolean()) {
					return "a boolean (" + s + ")";
				}
			}

			return s;
		}
	}

	public static String abbreviateMiddle(String str, String middle, int length) {
		if (JSONUtils.isEmpty(str) || JSONUtils.isEmpty(middle)) {
			return str;
		}

		if (length >= str.length() || length < (middle.length() + 2)) {
			return str;
		}

		int targetSting = length - middle.length();
		int startOffset = targetSting / 2 + targetSting % 2;
		int endOffset = str.length() - targetSting / 2;

		StrBuilder builder = new StrBuilder(length);
		builder.append(str.substring(0, startOffset));
		builder.append(middle);
		builder.append(str.substring(endOffset));

		return builder.toString();
	}

	public static <T> T fromJson(Gson gsonIn, Reader readerIn, Class<T> adapter, boolean lenient) {
		try {
			JsonReader jsonreader = new JsonReader(readerIn);
			jsonreader.setLenient(lenient);
			return gsonIn.getAdapter(adapter).read(jsonreader);
		} catch (IOException ioexception) {
			throw new JsonParseException(ioexception);
		}
	}

	public static <T> T fromJSON(Gson gson, Reader reader, TypeToken<T> type, boolean lenient) {
		try {
			JsonReader jsonreader = new JsonReader(reader);
			jsonreader.setLenient(lenient);
			return gson.getAdapter(type).read(jsonreader);
		} catch (IOException ioexception) {
			throw new JsonParseException(ioexception);
		}
	}

	public static <T> T fromJSON(Gson gson, String string, TypeToken<T> type, boolean lenient) {
		return JSONUtils.fromJSON(gson, new StringReader(string), type, lenient);
	}

	public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter, boolean lenient) {
		return JSONUtils.fromJson(gsonIn, new StringReader(json), adapter, lenient);
	}

	public static <T> T fromJSONUnlenient(Gson gson, Reader reader, TypeToken<T> type) {
		return JSONUtils.fromJSON(gson, reader, type, false);
	}

	public static <T> T fromJSONUnlenient(Gson gson, String string, TypeToken<T> type) {
		return JSONUtils.fromJSON(gson, string, type, false);
	}

	public static <T> T fromJson(Gson gson, Reader reader, Class<T> jsonClass) {
		return JSONUtils.fromJson(gson, reader, jsonClass, false);
	}

	public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter) {
		return JSONUtils.fromJson(gsonIn, json, adapter, false);
	}

	public static JsonObject fromJson(String json, boolean lenient) {
		return JSONUtils.fromJson(new StringReader(json), lenient);
	}

	public static JsonObject fromJson(Reader reader, boolean lenient) {
		return JSONUtils.fromJson(JSONUtils.GSON, reader, JsonObject.class, lenient);
	}

	public static JsonObject fromJson(String json) {
		return JSONUtils.fromJson(json, false);
	}

	public static JsonObject fromJson(Reader reader) {
		return JSONUtils.fromJson(reader, false);
	}
}
