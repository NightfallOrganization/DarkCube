/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import com.google.common.reflect.TypeToken;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border.Shape;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DataManager {

    private static final Key K_TASK = new Key("PServer", "task");
    private static final PersistentDataType<String> T_TASK = PersistentDataTypes.STRING;
    private static final Pattern WB_LEGACY_PSERVER = Pattern.compile("(?<task>woolbattle)(?<data>\\d*x\\d*)");
    private final Database database;
    private volatile Border border;
    private volatile Set<String> woolbattleTasks;
    private Location jarPlate;
    private boolean jarEnabled;
    private boolean winter;

    public DataManager() {
        database = InjectionLayer.boot().instance(DatabaseProvider.class).database("lobbysystem_data");
        setDefault("spawn", Locations.toDocument(Locations.DEFAULT, false));
        setDefault("border", new Border(Shape.CIRCLE, 100, Locations.DEFAULT, null).serializeToDocument());
        setDefault("woolbattleNPCLocation", Locations.toDocument(Locations.DEFAULT, false));
        setDefault("dailyrewardNPCLocation", Locations.toDocument(Locations.DEFAULT, false));
        setDefault("woolbattleSpawn", Locations.toDocument(Locations.DEFAULT, false));
        setDefault("winter", Document.newJsonDocument().append("value", true));
        winter = database.get("winter").getBoolean("value");
        setDefault("jumpAndRunEnabled", Document.newJsonDocument().append("value", true));
        jarEnabled = database.get("jumpAndRunEnabled").getBoolean("value");
        setDefault("jumpAndRunSpawn", Locations.toDocument(Locations.DEFAULT, false));
        setDefault("jumpAndRunPlate", Locations.toDocument(Locations.DEFAULT, false));
        jarPlate = Locations.fromDocument(database.get("jumpAndRunPlate"), null);

        setDefault("woolbattleTasks", Document.newJsonDocument().append("tasks", new HashSet<String>()));
        try {
            border = Border.GSON.fromJson(database.get("border").serializeToString(), Border.class);
        } catch (Exception ex) {
            border = new Border(Shape.CIRCLE, Double.MAX_VALUE, null, null);
        }
        fetchWoolBattleTasks();
        new BukkitRunnable() {
            @Override public void run() {
                Document doc = database.get("border");
                if (doc == null) {
                    return;
                }
                border = Border.GSON.fromJson(doc.serializeToString(), Border.class);
                fetchWoolBattleTasks();
            }
        }.runTaskTimerAsynchronously(Lobby.getInstance(), 20 * 60 * 2, 20 * 60 * 2);
    }

    public static void convertPServer(PServerExecutor executor) {
        var taskName = executor.storage().get(K_TASK, T_TASK);
        if (taskName == null) return; // Not a GameServer
        if (executor.storage().has(InventoryGameServerSelection.SERVICE)) return; // Already converted
        var type = executor.type().join();
        if (type == PServerExecutor.Type.WORLD) return;
        var matcher = WB_LEGACY_PSERVER.matcher(taskName);
        if (!matcher.matches()) {
            System.err.println("Wrong pserver: " + taskName);
            return;
        }
        String task = matcher.group("task");
        String data = matcher.group("data");
        var entry = Lobby.getInstance().gameRegistry().entry(task, data);
        if (entry == null) {
            System.err.println("No entry registered for " + taskName);
            return;
        }
        System.out.println(executor.id() + " -> " + task + ": " + data);
        executor.storage().setAsync(K_TASK, T_TASK, task).join();
        executor.storage().setAsync(InventoryGameServerSelection.SERVICE, InventoryGameServerSelection.SERVICE_TYPE, entry).join();
    }

    public boolean isJumpAndRunEnabled() {
        return jarEnabled;
    }

    public void setJumpAndRunEnabled(boolean enabled) {
        jarEnabled = enabled;
        database.insert("jumpAndRunEnabled", Document.newJsonDocument().append("value", enabled));
    }

    public boolean isWinter() {
        return winter;
    }

    public void setWinter(boolean winter) {
        this.winter = winter;
        database.insert("winter", Document.newJsonDocument().append("value", winter));
    }

    private void setDefault(String key, Document val) {
        if (!database.contains(key)) database.insert(key, val);
    }

    public void fetchWoolBattleTasks() {
        woolbattleTasks = database.get("woolbattleTasks").readObject("tasks", new TypeToken<Set<String>>() {
            private static final long serialVersionUID = 1461778882147270573L;
        }.getType());
    }

    public Set<String> getWoolBattleTasks() {
        return woolbattleTasks;
    }

    public void setWoolBattleTasks(Set<String> tasks) {
        this.woolbattleTasks = tasks;
        database.insert("woolbattleTasks", Document.newJsonDocument().append("tasks", tasks));
    }

    public Location getWoolBattleSpawn() {
        return Locations.fromDocument(database.get("woolbattleSpawn"), null);
    }

    public void setWoolBattleSpawn(Location loc) {
        database.insert("woolbattleSpawn", Locations.toDocument(loc, false));
    }

    public Location getJumpAndRunSpawn() {
        return Locations.fromDocument(database.get("jumpAndRunSpawn"), null);
    }

    public void setJumpAndRunSpawn(Location loc) {
        database.insert("jumpAndRunSpawn", Locations.toDocument(loc, false));
    }

    public Location getJumpAndRunPlate() {
        return jarPlate;
    }

    public void setJumpAndRunPlate(Location loc) {
        jarPlate = loc;
        database.insert("jumpAndRunPlate", Locations.toDocument(loc, false));
    }

    public Location getWoolBattleNPCLocation() {
        return Locations.fromDocument(database.get("woolbattleNPCLocation"), null);
    }

    public void setWoolBattleNPCLocation(Location loc) {
        database.insert("woolbattleNPCLocation", Locations.toDocument(loc, false));
    }

    public Location getDailyRewardNPCLocation() {
        return Locations.fromDocument(database.get("dailyrewardNPCLocation"), null);
    }

    public void setDailyRewardNPCLocation(Location loc) {
        database.insert("dailyrewardNPCLocation", Locations.toDocument(loc, false));
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        database.insert("border", border.serializeToDocument());
        this.border = border;
    }

    public Location getSpawn() {
        return Locations.fromDocument(database.get("spawn"), null);
    }

    public void setSpawn(Location spawn) {
        database.insert("spawn", Locations.toDocument(spawn, false));
    }
}
