/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;


import eu.darkcube.system.woolmania.WoolMania;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WoolManiaPlayer {
    PersistentDataValue<Integer> level;
    PersistentDataValue<Integer> money;
    Plugin woolMania = WoolMania.getInstance();

    public WoolManiaPlayer(Player player){
        initializePersistentData(player);
    }

    public void initializePersistentData(Player player){
        level = new PersistentDataValue<>(new NamespacedKey(woolMania, "level"), Integer.class, player.getPersistentDataContainer(), 1);
        money = new PersistentDataValue<>(new NamespacedKey(woolMania, "money"), Integer.class, player.getPersistentDataContainer(), 0);
    }

    //<editor-fold desc="Getter">
    public Integer getLevel() {
        return level.getOrDefault();
    }

    public Integer getMoney() {
        return money.getOrDefault();
    }
    //</editor-fold>

    //<editor-fold desc="Adder">
    public void addLevel(Integer integer) {
        int currentLevel = level.getOrDefault();
        level.set(currentLevel + integer);
    }

    public void addMoney(Integer integer) {
        int currentMoney = money.getOrDefault();
        money.set(currentMoney + integer);
    }
    //</editor-fold>

    //<editor-fold desc="Remover">
    public void removeLevel(Integer integer) {
        int currentLevel = level.getOrDefault();
        level.set(currentLevel - integer);
    }

    public void removeMoney(Integer integer) {
        int currentMoney = money.getOrDefault();
        money.set(currentMoney - integer);
    }
    //</editor-fold>

}
