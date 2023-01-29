/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import com.google.common.reflect.TypeToken;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border.Shape;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

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

	public boolean isJumpAndRunEnabled() {
		return jarEnabled;
	}

	public void setJumpAndRunEnabled(boolean enabled) {
		jarEnabled = enabled;
		database.update("jumpAndRunEnabled", new JsonDocument("value", enabled));
	}

	public boolean isWinter() {
		return winter;
	}

	public void setWinter(boolean winter) {
		this.winter = winter;
		database.update("winter", new JsonDocument().append("value", winter));
	}

	private void setDefault(String key, JsonDocument val) {
		if (!database.contains(key))
			database.insert(key, val);
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

	public void setWoolBattleTasks(Set<String> tasks) {
		this.woolbattleTasks = tasks;
		database.update("woolbattleTasks", new JsonDocument().append("tasks", tasks));
	}

	public Location getWoolBattleSpawn() {
		return Locations.fromDocument(database.get("woolbattleSpawn"), null);
	}

	public void setWoolBattleSpawn(Location loc) {
		database.update("woolbattleSpawn", Locations.toDocument(loc, false));
	}

	public Location getJumpAndRunSpawn() {
		return Locations.fromDocument(database.get("jumpAndRunSpawn"), null);
	}

	public void setJumpAndRunSpawn(Location loc) {
		database.update("jumpAndRunSpawn", Locations.toDocument(loc, false));
	}

	public Location getJumpAndRunPlate() {
		return jarPlate;
	}

	public void setJumpAndRunPlate(Location loc) {
		jarPlate = loc;
		database.update("jumpAndRunPlate", Locations.toDocument(loc, false));
	}

	public Location getWoolBattleNPCLocation() {
		return Locations.fromDocument(database.get("woolbattleNPCLocation"), null);
	}

	public void setWoolBattleNPCLocation(Location loc) {
		database.update("woolbattleNPCLocation", Locations.toDocument(loc, false));
	}

	public Location getDailyRewardNPCLocation() {
		return Locations.fromDocument(database.get("dailyrewardNPCLocation"), null);
	}

	public void setDailyRewardNPCLocation(Location loc) {
		database.update("dailyrewardNPCLocation", Locations.toDocument(loc, false));
	}

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		database.update("border", border.serializeToDocument());
		this.border = border;
	}

	public Location getSpawn() {
		return Locations.fromDocument(database.get("spawn"), null);
	}

	public void setSpawn(Location spawn) {
		database.update("spawn", Locations.toDocument(spawn, false));
	}
}
