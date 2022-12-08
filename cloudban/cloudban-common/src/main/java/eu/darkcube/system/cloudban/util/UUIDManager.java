/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.*;

public class UUIDManager {

	public static UUID getUUID(String playername) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + playername;
		try {
			StringBuilder UUIDJson = new StringBuilder();
			BufferedReader r = new BufferedReader(
					new InputStreamReader(new URL(url).openConnection().getInputStream()));
			String line;
			while ((line = r.readLine()) != null) {
				UUIDJson.append(line);
			}

//			IOUtils.toString(new URL(url));

			if (UUIDJson.toString().isEmpty())
				throw new InvalidNameException(playername);
			JsonObject UUIDObject = new Gson().fromJson(UUIDJson.toString(), JsonObject.class);
			String id = UUIDObject.get("id").getAsString();
			id = id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
			return UUID.fromString(id);
		} catch (IOException ex) {
			if (!(ex instanceof FileNotFoundException))
				ex.printStackTrace();
		}
		return null;
//		throw new NullPointerException("The UUID for '" + playername + "' could not be fetched, uuid=null");
	}

	public static CompletableFuture<UUID> getUUIDAsync(String playername) {
		return CompletableFuture.supplyAsync(() -> getUUID(playername));
	}

	public static String getPlayerName(UUID uuid) {
		String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
		try {
			BufferedReader r = new BufferedReader(
					new InputStreamReader(new URL(url).openConnection().getInputStream()));
			StringBuilder nameJson = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				nameJson.append(line);
			}
//					IOUtils.toString(new URL(url));
			JsonArray nameHistory = new Gson().fromJson(nameJson.toString(), JsonArray.class);
			String lastNameJson = nameHistory.get(nameHistory.size() - 1).toString();
			JsonObject nameObject = new Gson().fromJson(lastNameJson, JsonObject.class);
			return nameObject.get("name").getAsString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static CompletableFuture<String> getPlayerNameAsync(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> getPlayerName(uuid));
	}

	public static class InvalidNameException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		private String name;

		public InvalidNameException(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String getMessage() {
			return "The name " + name + " is not a valid name";
		}
	}
}
