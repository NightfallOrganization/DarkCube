/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game.items;

import eu.darkcube.system.sumo.game.GameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifeManager implements Listener {
    private final Map<String, Integer> lives = new HashMap<>(); // Teamnamen als Schlüssel
    private GameScoreboard gameScoreboard;

    public LifeManager(GameScoreboard gameScoreboard) {
        this.gameScoreboard = gameScoreboard;
    }

    public int getLives(String teamName) {
        return lives.getOrDefault(teamName, 10);
    }

    public void setLives(String teamName, int amount) {
        lives.put(teamName, amount);
    }
}
