/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.gson;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.DriverEnvironment;
import eu.darkcube.system.cloudban.util.ban.Ban;
import eu.darkcube.system.cloudban.util.ban.BanType;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.DateTime;
import eu.darkcube.system.cloudban.util.ban.Duration;
import eu.darkcube.system.cloudban.util.ban.JsonSerializer;
import eu.darkcube.system.cloudban.util.ban.Reason;
import eu.darkcube.system.cloudban.util.ban.Server;

public class BanSerializer extends JsonSerializer<Ban> {

	@Override
	public JsonElement serialize(Ban ban, Type type, JsonSerializationContext context) {
		UUID uuid = ban.getUUID();
		DateTime timeBanned = ban.getTimeBanned();
		Duration duration = ban.getDuration();
		UUID bannedBy = ban.getBannedBy();
		BanType banType = ban.getBanType();
		Reason reason = ban.getReason();
		Server server = ban.getServer();
		StringBuilder b = new StringBuilder();
		b.append(uuid.toString())
				.append(":")
				.append(BanUtil.GSON.toJson(timeBanned))
				.append(":")
				.append(BanUtil.GSON.toJson(duration))
				.append(":")
				.append(bannedBy.toString())
				.append(":")
				.append(banType.name())
				.append(":")
				.append(reason.getKey())
				.append(":")
				.append(server.getServer());
		return new JsonPrimitive(b.toString());
	}

	@Override
	public Ban deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		String[] args = json.getAsString().split(":");
		UUID uuid = UUID.fromString(args[0]);
		DateTime timeBanned = BanUtil.GSON.fromJson(args[1], DateTime.class);
		Duration duration = BanUtil.GSON.fromJson(args[2], Duration.class);
		UUID bannedBy = UUID.fromString(args[3]);
		BanType banType = BanType.valueOf(args[4]);
		Reason reason = getReason(args[5]);
		Server server = new Server(args[6]);
		return new Ban(uuid, timeBanned, duration, server, banType, reason, bannedBy);
	}

	private Reason getReason(String key) {
//		return new Reason(key, key);
		if (CloudNetDriver.getInstance().getDriverEnvironment() == DriverEnvironment.CLOUDNET) {
			return BanUtil.getReasons()
					.stream()
					.filter(r -> r.getKey().equals(key))
					.findFirst()
					.orElseGet(() -> new Reason(key, key));
		}
		return BanUtil.getReasons()
				.stream()
				.filter(r -> r.getKey().equals(key))
				.findFirst()
				.orElseGet(() -> new Reason(key, key));
	}
}
