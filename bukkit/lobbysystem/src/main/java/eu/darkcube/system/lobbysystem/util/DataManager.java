/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border.Shape;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Location;

public class DataManager {

    private static final Key K_TASK = Key.key("pserver", "task");
    private static final PersistentDataType<String> T_TASK = PersistentDataTypes.STRING;
    private static final Pattern WB_LEGACY_PSERVER = Pattern.compile("(?<task>woolbattle)(?<data>\\d*x\\d*)");
    private static final PersistentDataType<Set<String>> TYPE_TASKS = PersistentDataTypes.set(PersistentDataTypes.STRING);
    private final Lobby lobby;
    private final Key keySpawn;
    private final Key keyBorder;
    private final Key keyWoolBattleNPCLocation;
    private final Key keyWoolBattleModernNPCLocation;
    private final Key keySumoNPCLocation;
    private final Key keyDailyRewardNPCLocation;
    private final Key keyFisherNPCLocation;
    private final Key keyWoolBattleSpawn;
    private final Key keyWoolBattleModernSpawn;
    private final Key keySumoSpawn;
    private final Key keyFisherSpawn;
    private final Key keyWinter;
    private final Key keyJumpAndRunEnabled;
    private final Key keyJumpAndRunSpawn;
    private final Key keyJumpAndRunPlate;
    private final Key keyWoolBattleTasks;
    private final Key keyWoolBattleModernTasks;
    private final Key keySumoTasks;

    public DataManager(Lobby lobby) {
        this.lobby = lobby;
        this.keySpawn = Key.key(lobby, "spawn");
        this.keyBorder = Key.key(lobby, "border");
        this.keyWoolBattleNPCLocation = Key.key(lobby, "woolbattle_npc_location");
        this.keyWoolBattleModernNPCLocation = Key.key(lobby, "woolbattle_modern_npc_location");
        this.keySumoNPCLocation = Key.key(lobby, "sumo_npc_location");
        this.keyDailyRewardNPCLocation = Key.key(lobby, "daily_reward_npc_location");
        this.keyFisherNPCLocation = Key.key(lobby, "fisher_npc_location");
        this.keyWoolBattleSpawn = Key.key(lobby, "woolbattle_spawn");
        this.keyWoolBattleModernSpawn = Key.key(lobby, "woolbattle_modern_spawn");
        this.keySumoSpawn = Key.key(lobby, "sumo_spawn");
        this.keyFisherSpawn = Key.key(lobby, "fisher_spawn");
        this.keyWinter = Key.key(lobby, "winter");
        this.keyJumpAndRunEnabled = Key.key(lobby, "jump_and_run_enabled");
        this.keyJumpAndRunSpawn = Key.key(lobby, "jump_and_run_spawn");
        this.keyJumpAndRunPlate = Key.key(lobby, "jump_and_run_plate");
        this.keyWoolBattleTasks = Key.key(lobby, "woolbattle_tasks");
        this.keyWoolBattleModernTasks = Key.key(lobby, "woolbattle_modern_tasks");
        this.keySumoTasks = Key.key(lobby, "sumo_tasks");

        // Getting all these values also sets default values
        getSpawn();
        getBorder();
        getWoolBattleNPCLocation();
        getWoolBattleModernNPCLocation();
        getSumoNPCLocation();
        getDailyRewardNPCLocation();
        getFisherNPCLocation();
        getWoolBattleSpawn();
        getSumoSpawn();
        getFisherSpawn();
        isWinter();
        isJumpAndRunEnabled();
        getJumpAndRunSpawn();
        getJumpAndRunPlate();
        getWoolBattleTasks();
        getSumoTasks();
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
        var task = matcher.group("task");
        var data = matcher.group("data");
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
        return lobby.persistentDataStorage().get(keyJumpAndRunEnabled, PersistentDataTypes.BOOLEAN, () -> true);
    }

    public void setJumpAndRunEnabled(boolean enabled) {
        lobby.persistentDataStorage().set(keyJumpAndRunEnabled, PersistentDataTypes.BOOLEAN, enabled);
    }

    public boolean isWinter() {
        return lobby.persistentDataStorage().get(keyWinter, PersistentDataTypes.BOOLEAN, () -> true);
    }

    public void setWinter(boolean winter) {
        lobby.persistentDataStorage().set(keyWinter, PersistentDataTypes.BOOLEAN, winter);
    }

    public @Unmodifiable Set<String> getWoolBattleTasks() {
        return lobby.persistentDataStorage().get(keyWoolBattleTasks, TYPE_TASKS, HashSet::new);
    }

    public void setWoolBattleTasks(Set<String> tasks) {
        lobby.persistentDataStorage().set(keyWoolBattleTasks, TYPE_TASKS, tasks);
    }

    public @Unmodifiable Set<String> getWoolBattleModernTasks() {
        return lobby.persistentDataStorage().get(keyWoolBattleModernTasks, TYPE_TASKS, HashSet::new);
    }

    public void setWoolBattleModernTasks(Set<String> tasks) {
        lobby.persistentDataStorage().set(keyWoolBattleModernTasks, TYPE_TASKS, tasks);
    }

    public @Unmodifiable Set<String> getSumoTasks() {
        return lobby.persistentDataStorage().get(keySumoTasks, TYPE_TASKS, HashSet::new);
    }

    public void setSumoTasks(Set<String> tasks) {
        lobby.persistentDataStorage().set(keySumoTasks, TYPE_TASKS, tasks);
    }

    public void setSumoSpawn(Location loc) {
        lobby.persistentDataStorage().set(keySumoSpawn, Locations.TYPE, loc);
    }

    public Location getSumoSpawn() {
        return lobby.persistentDataStorage().get(keySumoSpawn, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setFisherSpawn(Location loc) {
        lobby.persistentDataStorage().set(keyFisherSpawn, Locations.TYPE, loc);
    }

    public Location getFisherSpawn() {
        return lobby.persistentDataStorage().get(keyFisherSpawn, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public Location getWoolBattleSpawn() {
        return lobby.persistentDataStorage().get(keyWoolBattleSpawn, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setWoolBattleSpawn(Location loc) {
        lobby.persistentDataStorage().set(keyWoolBattleSpawn, Locations.TYPE, loc);
    }

    public Location getWoolBattleModernSpawn() {
        return lobby.persistentDataStorage().get(keyWoolBattleModernSpawn, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setWoolBattleModernSpawn(Location loc) {
        lobby.persistentDataStorage().set(keyWoolBattleModernSpawn, Locations.TYPE, loc);
    }

    public Location getJumpAndRunSpawn() {
        return lobby.persistentDataStorage().get(keyJumpAndRunSpawn, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setJumpAndRunSpawn(Location loc) {
        lobby.persistentDataStorage().set(keyJumpAndRunSpawn, Locations.TYPE, loc);
    }

    public Location getJumpAndRunPlate() {
        return lobby.persistentDataStorage().get(keyJumpAndRunPlate, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setJumpAndRunPlate(Location loc) {
        lobby.persistentDataStorage().set(keyJumpAndRunPlate, Locations.TYPE, loc);
    }

    public Location getWoolBattleNPCLocation() {
        return lobby.persistentDataStorage().get(keyWoolBattleNPCLocation, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public Location getWoolBattleModernNPCLocation() {
        return lobby.persistentDataStorage().get(keyWoolBattleModernNPCLocation, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setWoolBattleNPCLocation(Location loc) {
        lobby.persistentDataStorage().set(keyWoolBattleNPCLocation, Locations.TYPE, loc);
    }

    public void setWoolBattleModernNPCLocation(Location loc) {
        lobby.persistentDataStorage().set(keyWoolBattleModernNPCLocation, Locations.TYPE, loc);
    }

    public Location getSumoNPCLocation() {
        return lobby.persistentDataStorage().get(keySumoNPCLocation, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setSumoNPCLocation(Location loc) {
        lobby.persistentDataStorage().set(keySumoNPCLocation, Locations.TYPE, loc);
    }

    public Location getDailyRewardNPCLocation() {
        return lobby.persistentDataStorage().get(keyDailyRewardNPCLocation, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setDailyRewardNPCLocation(Location loc) {
        lobby.persistentDataStorage().set(keyDailyRewardNPCLocation, Locations.TYPE, loc);
    }

    public Location getFisherNPCLocation() {
        return lobby.persistentDataStorage().get(keyFisherNPCLocation, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setFisherNPCLocation(Location loc) {
        lobby.persistentDataStorage().set(keyFisherNPCLocation, Locations.TYPE, loc);
    }

    public Border getBorder() {
        return lobby.persistentDataStorage().get(keyBorder, Border.TYPE, () -> new Border(Shape.CIRCLE, 100, Locations.DEFAULT, null));
    }

    public void setBorder(Border border) {
        lobby.persistentDataStorage().set(keyBorder, Border.TYPE, border);
    }

    public Location getSpawn() {
        return lobby.persistentDataStorage().get(keySpawn, Locations.TYPE, () -> Locations.DEFAULT);
    }

    public void setSpawn(Location spawn) {
        lobby.persistentDataStorage().set(keySpawn, Locations.TYPE, spawn);
    }
}
