/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Sounds;
import eu.darkcube.system.woolmania.enums.TeleportLocations;
import eu.darkcube.system.woolmania.enums.hall.Hall;
import io.leangen.geantyref.TypeToken;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class WoolManiaPlayer {
    PersistentDataValue<Integer> level;
    PersistentDataValue<Integer> money;
    PersistentDataValue<Integer> xp;
    PersistentDataValue<Integer> farmedBlocks;
    PersistentDataValue<Integer> privateBooster;
    PersistentDataValue<Sounds> farmingSound;
    PersistentDataValue<List<Sounds>> unlockedSounds;
    Player player;
    WoolMania woolMania = WoolMania.getInstance();
    @Nullable
    Hall hall;

    public WoolManiaPlayer(Player player) {
        this.player = player;
        initializePersistentData(player);
    }

    private void initializePersistentData(Player player) {
        level = new PersistentDataValue<>(new NamespacedKey(woolMania, "level"), Integer.class, player.getPersistentDataContainer(), 1);
        money = new PersistentDataValue<>(new NamespacedKey(woolMania, "money"), Integer.class, player.getPersistentDataContainer(), 0);
        xp = new PersistentDataValue<>(new NamespacedKey(woolMania, "xp"), Integer.class, player.getPersistentDataContainer(), 0);
        farmedBlocks = new PersistentDataValue<>(new NamespacedKey(woolMania, "farmedBlocks"), Integer.class, player.getPersistentDataContainer(), 0);
        privateBooster = new PersistentDataValue<>(new NamespacedKey(woolMania, "privateBooster"), Integer.class, player.getPersistentDataContainer(), 1);
        farmingSound = new PersistentDataValue<>(new NamespacedKey(woolMania, "farmingSound"), Sounds.class, player.getPersistentDataContainer(), Sounds.FARMING_SOUND_STANDARD);
        unlockedSounds = new PersistentDataValue<>(new NamespacedKey(woolMania, "unlockedSounds"), new TypeToken<List<Sounds>>() {
        }.getType(), player.getPersistentDataContainer(),  Sounds.unlockedSounds());
    }

    private void updateHall(@Nullable Hall hall) {
        this.hall = hall;
    }

    public void updateHallByWorldChange(@Nullable Hall hall) {
        updateHall(hall);
        woolMania.getGameScoreboard().updateWorld(player);
    }

    //<editor-fold desc="Teleport">
    public void teleportTo(Hall hall) {
        updateHall(hall);
        player.teleportAsync(hall.getSpawn().getLocation());
        woolMania.getGameScoreboard().updateWorld(player);
    }

    public void teleportSyncTo(Hall hall) {
        updateHall(hall);
        player.teleport(hall.getSpawn().getLocation());
        woolMania.getGameScoreboard().updateWorld(player);
    }

    public void teleportToSpawn() {
        updateHall(hall);
        player.teleportAsync(TeleportLocations.SPAWN.getLocation());
        woolMania.getGameScoreboard().updateWorld(player);
    }

    public void teleportSyncToSpawn() {
        updateHall(hall);
        player.teleport(TeleportLocations.SPAWN.getLocation());
        woolMania.getGameScoreboard().updateWorld(player);
    }
    //</editor-fold>

    //<editor-fold desc="Getter">
    public Integer getLevel() {
        return level.getOrDefault();
    }

    public Integer getMoney() {
        return money.getOrDefault();
    }

    public Integer getXP() {
        return xp.getOrDefault();
    }

    public Integer getFarmedBlocks() {
        return farmedBlocks.getOrDefault();
    }

    public Integer getPrivateBooster() {
        return privateBooster.getOrDefault();
    }

    public Sounds getFarmingSound() {
        return farmingSound.getOrDefault();
    }

    @Nullable
    public Hall getHall() {
        return hall;
    }

    public boolean isSoundUnlocked(Sounds sound) {
        return unlockedSounds.getOrDefault().contains(sound);
    }
    //</editor-fold>

    //<editor-fold desc="Setter">
    public void setLevel(Integer integer, Player player) {
        level.set(integer);
        WoolMania.getInstance().getGameScoreboard().updateLevel(player);
    }

    public void setMoney(Integer integer, Player player) {
        money.set(integer);
        WoolMania.getInstance().getGameScoreboard().updateMoney(player);
    }

    public void setHall(Hall hall) {
        if (this.hall != null) throw new IllegalStateException();
        updateHall(hall);
        woolMania.getGameScoreboard().updateWorld(player);
    }

    public void setXP(Integer integer) {
        xp.set(integer);
    }

    public void setFarmedBlocks(Integer integer, Player player) {
        farmedBlocks.set(integer);
        WoolMania.getInstance().getGameScoreboard().updateFarmed(player);
    }

    public void setPrivateBooster(Integer integer, Player player) {
        privateBooster.set(integer);
        WoolMania.getInstance().getGameScoreboard().updateBooster(player);
    }

    public void setFarmingSound(Sounds sound) {
        farmingSound.set(sound);
    }

    public void resetDefaultFarmingSound() {
        farmingSound.reset();
    }

    //</editor-fold>

    //<editor-fold desc="Adder">
    public void addLevel(Integer integer, Player player) {
        int currentLevel = level.getOrDefault();
        level.set(currentLevel + integer);
        WoolMania.getInstance().getGameScoreboard().updateLevel(player);
    }

    public void addMoney(Integer integer, Player player) {
        int currentMoney = money.getOrDefault();
        money.set(currentMoney + integer);
        WoolMania.getInstance().getGameScoreboard().updateMoney(player);
    }

    public void addXP(Integer integer) {
        int currentXP = xp.getOrDefault();
        xp.set(currentXP + integer);
    }

    public void addFarmedBlocks(Integer integer, Player player) {
        int currentFarmedBlocks = farmedBlocks.getOrDefault();
        farmedBlocks.set(currentFarmedBlocks + integer);
        WoolMania.getInstance().getGameScoreboard().updateFarmed(player);
    }

    public void addPrivateBooster(Integer integer, Player player) {
        int currentPrivateBooster = privateBooster.getOrDefault();
        privateBooster.set(currentPrivateBooster + integer);
        WoolMania.getInstance().getGameScoreboard().updateBooster(player);
    }
    //</editor-fold>

    //<editor-fold desc="Remover">
    public void removeLevel(Integer integer, Player player) {
        int currentLevel = level.getOrDefault();
        level.set(currentLevel - integer);
        WoolMania.getInstance().getGameScoreboard().updateLevel(player);
    }

    public void removeMoney(Integer integer, Player player) {
        int currentMoney = money.getOrDefault();
        money.set(currentMoney - integer);
        WoolMania.getInstance().getGameScoreboard().updateMoney(player);
    }

    public void removeXP(Integer integer, Player player) {
        int currentXP = xp.getOrDefault();
        xp.set(currentXP - integer);
    }

    public void removeFarmedBlocks(Integer integer, Player player) {
        int currentFarmedBlocks = farmedBlocks.getOrDefault();
        farmedBlocks.set(currentFarmedBlocks - integer);
        WoolMania.getInstance().getGameScoreboard().updateFarmed(player);
    }

    public void removePrivateBooster(Integer integer, Player player) {
        int currentPrivateBooster = privateBooster.getOrDefault();
        privateBooster.set(currentPrivateBooster - integer);
        WoolMania.getInstance().getGameScoreboard().updateBooster(player);
    }

    public void unlockSound(Sounds sound) {
        List<Sounds> sounds = new ArrayList<>(unlockedSounds.getOrDefault());
        sounds.add(sound);
        unlockedSounds.set(sounds);
    }

    public void resetSounds() {
        unlockedSounds.reset();
    }

    //</editor-fold>

}
