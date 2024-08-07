/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import building.oneblock.OneBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerWorldManager {

    private static final NamespacedKey WORLDS_OWNED_KEY = new NamespacedKey(OneBlock.getInstance(), "worlds_owned");
    private static final NamespacedKey WORLDS_INVITED_KEY = new NamespacedKey(OneBlock.getInstance(), "worlds_invited");

    public void addWorldToPlayer(Player player, String worldName, boolean isOwner) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = isOwner ? WORLDS_OWNED_KEY : WORLDS_INVITED_KEY;
        String existingWorlds = data.getOrDefault(key, PersistentDataType.STRING, "");
        if (!existingWorlds.contains(worldName + ";")) {
            data.set(key, PersistentDataType.STRING, existingWorlds + worldName + ";");
        }
    }

    public void removeWorldFromPlayer(Player player, String worldName, boolean isOwner) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = isOwner ? WORLDS_OWNED_KEY : WORLDS_INVITED_KEY;
        String worlds = data.getOrDefault(key, PersistentDataType.STRING, "");
        String updatedWorlds = worlds.replace(worldName + ";", "");
        data.set(key, PersistentDataType.STRING, updatedWorlds);
    }

    public String[] getWorlds(Player player, boolean isOwner) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = isOwner ? WORLDS_OWNED_KEY : WORLDS_INVITED_KEY;
        String worlds = data.getOrDefault(key, PersistentDataType.STRING, "");
        return worlds.split(";");
    }
}
