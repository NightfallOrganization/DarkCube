/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager;

import building.oneblock.OneBlock;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.World;

import java.io.File;

public class WorldSlotManager {
    public static final int MAX_SLOTS = 5;
    private static final NamespacedKey WORLD_SLOTS_KEY = new NamespacedKey(OneBlock.getInstance(), "world_slots");
    private String[] slots = new String[MAX_SLOTS];

    public void loadSlots(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        for (int i = 0; i < MAX_SLOTS; i++) {
            String worldName = data.get(new NamespacedKey(WORLD_SLOTS_KEY.getNamespace(), WORLD_SLOTS_KEY.getKey() + "_slot" + i), PersistentDataType.STRING);
            slots[i] = worldName != null ? worldName : "empty";
        }
    }

    public void setWorld(int slot, World world, Player player) {
        if (slot >= 0 && slot < MAX_SLOTS) {
            String worldName = world != null ? world.getName() : null;
            slots[slot] = worldName;
            PersistentDataContainer data = player.getPersistentDataContainer();
            if (worldName == null) {
                data.remove(new NamespacedKey(WORLD_SLOTS_KEY.getNamespace(), WORLD_SLOTS_KEY.getKey() + "_slot" + slot));
            } else {
                data.set(new NamespacedKey(WORLD_SLOTS_KEY.getNamespace(), WORLD_SLOTS_KEY.getKey() + "_slot" + slot), PersistentDataType.STRING, worldName);
            }
        } else {
            throw new IllegalArgumentException("Slot index out of bounds");
        }
    }

    public String getWorld(int slot) {
        if (slot >= 0 && slot < MAX_SLOTS) {
            return slots[slot];
        } else {
            throw new IllegalArgumentException("Slot index out of bounds");
        }
    }

    public void validateWorlds(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        for (int i = 0; i < MAX_SLOTS; i++) {
            String worldName = slots[i];
            if (worldName != null && !worldName.equals("empty")) {
                World world = Bukkit.getWorld(worldName);
                if (world == null && !new File(Bukkit.getWorldContainer(), worldName).exists()) {
                    slots[i] = "empty";
                    data.remove(new NamespacedKey(WORLD_SLOTS_KEY.getNamespace(), WORLD_SLOTS_KEY.getKey() + "_slot" + i));
                }
            }
        }
    }

    public void deleteWorld(int slot, Player player) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            throw new IllegalArgumentException("Slot index out of bounds");
        }

        PersistentDataContainer data = player.getPersistentDataContainer();
        data.remove(new NamespacedKey(WORLD_SLOTS_KEY.getNamespace(), WORLD_SLOTS_KEY.getKey() + "_slot" + slot));
        slots[slot] = "empty";
    }

}

