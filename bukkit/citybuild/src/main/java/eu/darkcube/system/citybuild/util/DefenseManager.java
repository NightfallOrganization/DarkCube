/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.listener.Citybuild;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DefenseManager {
    private Citybuild plugin;
    private static final NamespacedKey DEFENSE_KEY = new NamespacedKey(Citybuild.getInstance(), "defense");
    private static final double DEFENSE_MULTIPLIER = 0.25;  // Sie können diesen Wert anpassen

    public DefenseManager(Citybuild plugin) {
        this.plugin = plugin;
    }

    public void addDefense(Player player, int defenseToAdd) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        double currentDefense = data.getOrDefault(DEFENSE_KEY, PersistentDataType.DOUBLE, 0.0);
        data.set(DEFENSE_KEY, PersistentDataType.DOUBLE, currentDefense + (defenseToAdd * DEFENSE_MULTIPLIER));
        System.out.println("Adding defense for " + player.getName());
    }

    public void resetDefense(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(DEFENSE_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    public double getDefense(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.getOrDefault(DEFENSE_KEY, PersistentDataType.DOUBLE, 0.0);
    }
}
