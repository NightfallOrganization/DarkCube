/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.friend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;

public class FriendUtil {

	private static Database database = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("friends");

	public static boolean acceptsFriendRequests(UUID uuid) {
		insertDefaults(uuid);
		return database.get(uuid.toString()).getBoolean("acceptsFriendRequests");
	}

	public static boolean setAcceptsFriendRequests(UUID uuid, boolean val) {
		insertDefaults(uuid);
		return database.update(uuid.toString(), database.get(uuid.toString()).append("acceptsFriendRequests", val));
	}

	public static Set<UUID> getFriends(UUID uuid) {
		if (!database.contains(uuid.toString())) {
			return new HashSet<>();
		}
		JsonDocument doc = database.get(uuid.toString());
		return fromString(doc.getString("friends"));
	}

	public static Set<UUID> getPending(UUID uuid) {
		if (!database.contains(uuid.toString())) {
			return new HashSet<>();
		}
		JsonDocument doc = database.get(uuid.toString());
		return fromString(doc.getString("pending"));
	}

	public static boolean setPending(UUID uuid, Set<UUID> pending) {
		insertDefaults(uuid);
		return database.update(uuid.toString(), database.get(uuid.toString()).append("pending", toString(pending)));
	}

	public static boolean isPending(UUID uuid, UUID pending) {
		return getPending(uuid).contains(pending);
	}

	private static void insertDefaults(UUID uuid) {
		if (!database.contains(uuid.toString())) {
			JsonDocument doc = new JsonDocument();
			doc.append("acceptsFriendRequests", true);
			doc.append("pending", toString(new HashSet<>()));
			doc.append("friends", toString(new HashSet<>()));
			doc.append("sentRequests", toString(new HashSet<>()));
			database.insert(uuid.toString(), doc);
		}
	}

	private static Set<UUID> fromString(String string) {
		if(string == null)
			string = "";
		String[] a = string.split(":");
		Set<UUID> ls = new HashSet<>();
		for (String s : a) {
			try {
				ls.add(UUID.fromString(s));
			} catch (Exception ex) {
			}
		}
		return ls;
	}

	private static String toString(Set<UUID> friends) {
		StringBuilder b = new StringBuilder();
		List<UUID> copy = new ArrayList<>(friends);
		for (int i = 0; i < copy.size(); i++) {
			b.append(copy.get(i).toString());
			if (i - 1 != friends.size()) {
				b.append(":");
			}
		}
		return b.toString();
	}

	public static boolean setFriends(UUID uuid, Set<UUID> friends) {
		insertDefaults(uuid);
		return database.update(uuid.toString(), database.get(uuid.toString()).append("friends", toString(friends)));
	}

	public static boolean addFriends(UUID uuid, UUID... friends) {
		Set<UUID> l = getFriends(uuid);
		l.addAll(Arrays.asList(friends));
		return setFriends(uuid, l);
	}

	public static boolean removeFriends(UUID uuid, UUID... friends) {
		Set<UUID> l = getFriends(uuid);
		l.removeAll(Arrays.asList(friends));
		return setFriends(uuid, l);
	}

	public static Set<UUID> getSentRequests(UUID uuid) {
		if (!database.contains(uuid.toString())) {
			return new HashSet<>();
		}
		JsonDocument doc = database.get(uuid.toString());
		return fromString(doc.getString("sentRequests"));
	}

	public static boolean setSentRequests(UUID uuid, Set<UUID> sent) {
		insertDefaults(uuid);
		return database.update(uuid.toString(), database.get(uuid.toString()).append("sentRequests", toString(sent)));
	}
}
