/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerDataHandler {
    private static File file;
    private static FileConfiguration playerData;

    public static void setup(File dataFolder) {
        file = new File(dataFolder, "playerdata.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace();
            }
        }

        playerData = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return playerData;
    }

    public static void save() {
        try {
            playerData.save(file);
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }
    }

    public static void reload() {
        playerData = YamlConfiguration.loadConfiguration(file);
    }

    public static HashMap<UUID, String> loadPlayers() {
        HashMap<UUID, String> players = new HashMap<>();
        if (playerData.isConfigurationSection("playersStepped")) {
            ConfigurationSection section = playerData.getConfigurationSection("playersStepped");
            for (String uuidStr : section.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                String location = section.getString(uuidStr);
                players.put(uuid, location);
            }
        }
        return players;
    }

}
