/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import building.oneblock.OneBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class DataStorageManager {

    // Schlüssel für die Persistent Data
    private final NamespacedKey ownerKey = new NamespacedKey(OneBlock.getInstance(), "world_owner");
    private final NamespacedKey invitedPlayersKey = new NamespacedKey(OneBlock.getInstance(), "invited_players");
    private final NamespacedKey blocksMinedKey = new NamespacedKey(OneBlock.getInstance(), "blocks_mined");

    // Methode zum Speichern des Besitzers der Welt
    public void setWorldOwner(World world, Player owner) {
        PersistentDataContainer data = world.getPersistentDataContainer();
        data.set(ownerKey, PersistentDataType.STRING, owner.getUniqueId().toString());
    }

    // Methode zum Abrufen des Besitzers der Welt
    public Player getWorldOwner(World world) {
        PersistentDataContainer data = world.getPersistentDataContainer();
        if (data.has(ownerKey, PersistentDataType.STRING)) {
            String ownerUUID = data.get(ownerKey, PersistentDataType.STRING);
            return OneBlock.getInstance().getServer().getPlayer(java.util.UUID.fromString(ownerUUID));
        }
        return null;
    }

    // Methode zum Speichern eingeladener Spieler
    public void setInvitedPlayers(World world, java.util.List<Player> invitedPlayers) {
        PersistentDataContainer data = world.getPersistentDataContainer();
        String playerUUIDs = invitedPlayers.stream()
                .map(player -> player.getUniqueId().toString())
                .collect(java.util.stream.Collectors.joining(","));
        data.set(invitedPlayersKey, PersistentDataType.STRING, playerUUIDs);
    }

    // Methode zum Abrufen eingeladener Spieler
    public java.util.List<Player> getInvitedPlayers(World world) {
        PersistentDataContainer data = world.getPersistentDataContainer();
        if (data.has(invitedPlayersKey, PersistentDataType.STRING)) {
            String[] playerUUIDs = data.get(invitedPlayersKey, PersistentDataType.STRING).split(",");
            java.util.List<Player> players = new java.util.ArrayList<>();
            for (String uuid : playerUUIDs) {
                Player player = OneBlock.getInstance().getServer().getPlayer(java.util.UUID.fromString(uuid));
                if (player != null) {
                    players.add(player);
                }
            }
            return players;
        }
        return java.util.Collections.emptyList();
    }

    // Methode zum Speichern der Anzahl abgebauter Blöcke
    public void setBlocksMined(World world, int blocksMined) {
        PersistentDataContainer data = world.getPersistentDataContainer();
        data.set(blocksMinedKey, PersistentDataType.INTEGER, blocksMined);
    }

    // Methode zum Abrufen der Anzahl abgebauter Blöcke
    public int getBlocksMined(World world) {
        PersistentDataContainer data = world.getPersistentDataContainer();
        return data.getOrDefault(blocksMinedKey, PersistentDataType.INTEGER, 0);
    }
}
