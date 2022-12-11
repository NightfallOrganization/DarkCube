/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border.Shape;

public class DataManager {

	private final Database database;
	private volatile Border border;
	private volatile Set<String> woolbattleTasks;
	private Location jarPlate;
	private boolean jarEnabled;
	private boolean winter;

	public DataManager() {
		database =
				CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("lobbysystem_data");
		setDefault("spawn", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("border",
				new Border(Shape.CIRCLE, 100, Locations.DEFAULT, null).serializeToDocument());
		setDefault("woolbattleNPCLocation", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("dailyrewardNPCLocation", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("woolbattleSpawn", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("winter", new JsonDocument().append("value", true));
		winter = database.get("winter").getBoolean("value");
		setDefault("jumpAndRunEnabled", new JsonDocument().append("value", true));
		jarEnabled = database.get("jumpAndRunEnabled").getBoolean("value");
		setDefault("jumpAndRunSpawn", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("jumpAndRunPlate", Locations.toDocument(Locations.DEFAULT, false));
		jarPlate = Locations.fromDocument(database.get("jumpAndRunPlate"), null);

		setDefault("woolbattleTasks", new JsonDocument().append("tasks", new HashSet<String>()));
		try {
			border = Border.GSON.fromJson(database.get("border").toJson(), Border.class);
		} catch (Exception ex) {
			border = new Border(Shape.CIRCLE, Double.MAX_VALUE, null, null);
		}
		fetchWoolBattleTasks();
		new BukkitRunnable() {
			@Override
			public void run() {
				JsonDocument doc = database.get("border");
				if (doc == null) {
					return;
				}
				border = Border.GSON.fromJson(doc.toJson(), Border.class);
				fetchWoolBattleTasks();
			}
		}.runTaskTimerAsynchronously(Lobby.getInstance(), 20 * 60 * 2, 20 * 60 * 2);
	}

	public void setJumpAndRunEnabled(boolean enabled) {
		jarEnabled = enabled;
		database.update("jumpAndRunEnabled", new JsonDocument("value", enabled));
	}

	public boolean isJumpAndRunEnabled() {
		return jarEnabled;
	}

	public void setWinter(boolean winter) {
		this.winter = winter;
		database.update("winter", new JsonDocument().append("value", winter));
	}

	public boolean isWinter() {
		return winter;
	}

	private void setDefault(String key, JsonDocument val) {
		if (!database.contains(key))
			database.insert(key, val);
	}

	public DataManager setBorder(Border border) {
		database.update("border", border.serializeToDocument());
		this.border = border;
		return this;
	}

	public void fetchWoolBattleTasks() {
		woolbattleTasks =
				database.get("woolbattleTasks").get("tasks", new TypeToken<Set<String>>() {
					private static final long serialVersionUID = 1461778882147270573L;
				}.getType());
	}

	public Set<String> getWoolBattleTasks() {
		return woolbattleTasks;
	}

	public DataManager setWoolBattleTasks(Set<String> tasks) {
		database.update("woolbattleTasks", new JsonDocument().append("tasks", tasks));
		return this;
	}

	public Location getWoolBattleSpawn() {
		return Locations.fromDocument(database.get("woolbattleSpawn"), null);
	}

	public Location getJumpAndRunSpawn() {
		return Locations.fromDocument(database.get("jumpAndRunSpawn"), null);
	}

	public DataManager setJumpAndRunSpawn(Location loc) {
		database.update("jumpAndRunSpawn", Locations.toDocument(loc, false));
		return this;
	}

	public Location getJumpAndRunPlate() {
		return jarPlate;
	}

	public DataManager setJumpAndRunPlate(Location loc) {
		jarPlate = loc;
		database.update("jumpAndRunPlate", Locations.toDocument(loc, false));
		return this;
	}

	public DataManager setWoolBattleSpawn(Location loc) {
		database.update("woolbattleSpawn", Locations.toDocument(loc, false));
		return this;
	}

	public DataManager setWoolBattleNPCLocation(Location loc) {
		database.update("woolbattleNPCLocation", Locations.toDocument(loc, false));
		return this;
	}

	public Location getWoolBattleNPCLocation() {
		return Locations.fromDocument(database.get("woolbattleNPCLocation"), null);
	}

	public DataManager setDailyRewardNPCLocation(Location loc) {
		database.update("dailyrewardNPCLocation", Locations.toDocument(loc, false));
		return this;
	}

	public Location getDailyRewardNPCLocation() {
		return Locations.fromDocument(database.get("dailyrewardNPCLocation"), null);
	}

	public Location toLocation(JsonDocument doc) {
		String d = doc.getString("p");
		String[] s = d.split(":");
		double x = Double.parseDouble(s[0]);
		double y = Double.parseDouble(s[1]);
		double z = Double.parseDouble(s[2]);
		float yaw = Float.parseFloat(s[3]);
		float pit = Float.parseFloat(s[4]);
		UUID uid = UUID.fromString(s[5]);
		World w = Bukkit.getWorld(uid);
		return new Location(w, x, y, z, yaw, pit);
	}

	public JsonDocument toDocument(Location loc) {
		JsonDocument doc = new JsonDocument();
		doc.append("p", loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":"
				+ loc.getPitch() + ":" + loc.getWorld().getUID());
		return doc;
	}

	public Border getBorder() {
		return border;
	}

	public DataManager setSpawn(Location spawn) {
		database.update("spawn", Locations.toDocument(spawn, false));
		return this;
	}

	public Location getSpawn() {
		return Locations.fromDocument(database.get("spawn"), null);
	}
}
