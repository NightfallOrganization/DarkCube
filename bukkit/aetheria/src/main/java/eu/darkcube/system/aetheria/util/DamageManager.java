/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DamageManager {
    private static final NamespacedKey DAMAGE_KEY = new NamespacedKey(Aetheria.getInstance(), "Damage");

    public void addDamage(Player player, int damageToAdd) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        double currentDamage = data.getOrDefault(DAMAGE_KEY, PersistentDataType.DOUBLE, 0.0);
        data.set(DAMAGE_KEY, PersistentDataType.DOUBLE, currentDamage + damageToAdd);
        System.out.println("Adding damage for " + player.getName());
    }

    public void resetDamage(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(DAMAGE_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    public double getDamage(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.getOrDefault(DAMAGE_KEY, PersistentDataType.DOUBLE, 0.0);
    }

}
