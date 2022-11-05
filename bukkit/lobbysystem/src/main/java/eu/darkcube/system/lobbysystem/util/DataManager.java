package eu.darkcube.system.lobbysystem.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.reflect.TypeToken;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border.Shape;

public class DataManager {

	private volatile Database database;
	private volatile Database databaseUserpos;
	private volatile Border border;
	private volatile Set<String> woolbattleTasks;

	public DataManager() {
		database = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("lobbysystem_data");
		databaseUserpos = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("lobbysystem_userpos");
		setDefault("spawn", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("border", new Border(Shape.CIRCLE, 100, Locations.DEFAULT,
						null).serializeToDocument());
		setDefault("woolbattleNPCLocation", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("dailyrewardNPCLocation", Locations.toDocument(Locations.DEFAULT, false));
		setDefault("woolbattleSpawn", Locations.toDocument(Locations.DEFAULT, false));
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
		}.runTaskTimerAsynchronously(Lobby.getInstance(), 20 * 60 * 2, 20 * 60
						* 2);
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

	public Database getDatabaseUserpos() {
		return databaseUserpos;
	}

	public void setUserPos(UUID uuid, Location loc) {
		if (!databaseUserpos.contains(uuid.toString())) {
			databaseUserpos.insert(uuid.toString(), toDocument(loc));
		} else {
			databaseUserpos.update(uuid.toString(), toDocument(loc));
		}
	}

	public Location getUserPos(UUID uuid) {
		Location p;
		try {
			p = toLocation(databaseUserpos.get(uuid.toString()));
		} catch (Exception ex) {
			p = null;
		}
		if (p == null || p.getWorld() == null) {
			p = getSpawn();
		}
		return p;
	}

	public void fetchWoolBattleTasks() {
		woolbattleTasks = database.get("woolbattleTasks").get("tasks", new TypeToken<Set<String>>() {
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
		doc.append("p", loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":"
						+ loc.getYaw() + ":" + loc.getPitch() + ":"
						+ loc.getWorld().getUID());
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