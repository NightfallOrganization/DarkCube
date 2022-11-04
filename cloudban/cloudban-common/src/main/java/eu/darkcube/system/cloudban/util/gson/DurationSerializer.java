package eu.darkcube.system.cloudban.util.gson;

import java.lang.reflect.Type;
import java.math.BigInteger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import eu.darkcube.system.cloudban.util.ban.Duration;
import eu.darkcube.system.cloudban.util.ban.JsonSerializer;

public class DurationSerializer extends JsonSerializer<Duration> {

	@Override
	public JsonElement serialize(Duration duration, Type type, JsonSerializationContext context) {
		return new JsonPrimitive(duration.getDurationInSeconds().toString());
	}

	@Override
	public Duration deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return new Duration(new BigInteger(json.getAsString()));
	}
}
