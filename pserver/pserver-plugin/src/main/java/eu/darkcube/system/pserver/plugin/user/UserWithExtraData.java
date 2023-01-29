/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import java.util.ArrayList;
import java.util.Map.*;
import java.util.UUID;

import com.google.gson.*;

public abstract class UserWithExtraData implements User {

	private JsonObject extra;
	private final UUID uuid;

	public UserWithExtraData(UUID uuid) {
		this.uuid = uuid;
		this.setExtra(UserCache.cache().getEntry(uuid).extra);
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public JsonObject getExtra() {
		return extra;
	}

	@Override
	public void setExtra(JsonObject extra) {
		this.extra = extra == null ? new JsonObject() : extra;
	}

	@Override
	public void saveExtra() {
		// Saving extra to UserCache
		UserCache.Entry entry = UserCache.cache().getEntry(uuid);
		for (Entry<String, JsonElement> e : new ArrayList<>(entry.extra.entrySet())) {
			entry.extra.remove(e.getKey());
		}
		for (Entry<String, JsonElement> e : extra.entrySet()) {
			entry.extra.add(e.getKey(), e.getValue());
		}
		UserCache.cache().update(entry, true);
	}
}
