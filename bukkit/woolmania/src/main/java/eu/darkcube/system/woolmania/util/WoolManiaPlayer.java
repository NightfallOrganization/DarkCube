/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import eu.darkcube.system.woolmania.WoolMania;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WoolManiaPlayer {
    PersistentDataValue<Integer> level;
    PersistentDataValue<Integer> money;
    PersistentDataValue<Integer> xp;
    PersistentDataValue<Integer> farmedBlocks;
    PersistentDataValue<Integer> privateBooster;
    Plugin woolMania = WoolMania.getInstance();

    public WoolManiaPlayer(Player player){
        initializePersistentData(player);
    }

    public void initializePersistentData(Player player){
        level = new PersistentDataValue<>(new NamespacedKey(woolMania, "level"), Integer.class, player.getPersistentDataContainer(), 1);
        money = new PersistentDataValue<>(new NamespacedKey(woolMania, "money"), Integer.class, player.getPersistentDataContainer(), 0);
        xp = new PersistentDataValue<>(new NamespacedKey(woolMania, "xp"), Integer.class, player.getPersistentDataContainer(), 0);
        farmedBlocks = new PersistentDataValue<>(new NamespacedKey(woolMania, "farmedBlocks"), Integer.class, player.getPersistentDataContainer(), 0);
        privateBooster = new PersistentDataValue<>(new NamespacedKey(woolMania, "privateBooster"), Integer.class, player.getPersistentDataContainer(), 1);
    }

    //<editor-fold desc="Updater">
    public void delayedUpdateLevel(Player player) {
        Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
            WoolMania.getInstance().getGameScoreboard().updateLevel(player);
        }, 1L);
    }

    public void delayedUpdateMoney(Player player) {
        Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
            WoolMania.getInstance().getGameScoreboard().updateMoney(player);
        }, 1L);
    }

    public void delayedUpdateFarmedBlocks(Player player) {
        Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
            WoolMania.getInstance().getGameScoreboard().updateFarmed(player);
        }, 1L);
    }

    public void delayedUpdatePrivateBooster(Player player) {
        Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
            WoolMania.getInstance().getGameScoreboard().updateBooster(player);
        }, 1L);
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
    //</editor-fold>

    //<editor-fold desc="Setter">
    public void setLevel(Integer integer, Player player) {
        delayedUpdateLevel(player);
        level.set(integer);
    }

    public void setMoney(Integer integer, Player player) {
        delayedUpdateMoney(player);
        money.set(integer);
    }

    public void setXP(Integer integer) {
        xp.set(integer);
    }

    public void setFarmedBlocks(Integer integer, Player player) {
        delayedUpdateFarmedBlocks(player);
        farmedBlocks.set(integer);
    }

    public void setPrivateBooster(Integer integer, Player player) {
        delayedUpdatePrivateBooster(player);
        privateBooster.set(integer);
    }
    //</editor-fold>

    //<editor-fold desc="Adder">
    public void addLevel(Integer integer, Player player) {
        delayedUpdateLevel(player);
        int currentLevel = level.getOrDefault();
        level.set(currentLevel + integer);
    }

    public void addMoney(Integer integer, Player player) {
        delayedUpdateMoney(player);
        int currentMoney = money.getOrDefault();
        money.set(currentMoney + integer);
    }

    public void addXP(Integer integer) {
        int currentXP = xp.getOrDefault();
        xp.set(currentXP + integer);
    }

    public void addFarmedBlocks(Integer integer, Player player) {
        delayedUpdateFarmedBlocks(player);
        int currentFarmedBlocks = farmedBlocks.getOrDefault();
        farmedBlocks.set(currentFarmedBlocks + integer);
    }

    public void addPrivateBooster(Integer integer, Player player) {
        delayedUpdatePrivateBooster(player);
        int currentPrivateBooster = privateBooster.getOrDefault();
        privateBooster.set(currentPrivateBooster + integer);
    }
    //</editor-fold>

    //<editor-fold desc="Remover">
    public void removeLevel(Integer integer, Player player) {
        delayedUpdateLevel(player);
        int currentLevel = level.getOrDefault();
        level.set(currentLevel - integer);
    }

    public void removeMoney(Integer integer, Player player) {
        delayedUpdateMoney(player);
        int currentMoney = money.getOrDefault();
        money.set(currentMoney - integer);
    }

    public void removeXP(Integer integer, Player player) {
        int currentXP = xp.getOrDefault();
        xp.set(currentXP - integer);
    }

    public void removeFarmedBlocks(Integer integer, Player player) {
        delayedUpdateFarmedBlocks(player);
        int currentFarmedBlocks = farmedBlocks.getOrDefault();
        farmedBlocks.set(currentFarmedBlocks - integer);
    }

    public void removePrivateBooster(Integer integer, Player player) {
        delayedUpdatePrivateBooster(player);
        int currentPrivateBooster = privateBooster.getOrDefault();
        privateBooster.set(currentPrivateBooster - integer);
    }
    //</editor-fold>

}
