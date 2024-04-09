/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.manager;

import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.*;

public class MapManager implements Listener {
    private World activeWorld;
    private JavaPlugin plugin;
    private LobbyScoreboard lobbyScoreboard;
    private static final List<String> AVAILABLE_MAPS = Arrays.asList("Origin", "Atlas", "Demonic");
    private final HashMap<String, Integer> mapVotes = new HashMap<>();
    private final HashMap<UUID, String> playerVotes = new HashMap<>();

    public MapManager(JavaPlugin plugin, LobbyScoreboard lobbyScoreboard) {
        this.plugin = plugin;
        this.lobbyScoreboard = lobbyScoreboard;
        setRandomMap();
        // Initialisiert die Votes mit 0 für alle verfügbaren Maps
        AVAILABLE_MAPS.forEach(map -> mapVotes.put(map, 0));
    }

    public void setRandomMap() {
        String worldName = AVAILABLE_MAPS.get(new Random().nextInt(AVAILABLE_MAPS.size()));
        setActiveWorld(Bukkit.getWorld(worldName));
    }

    public void setActiveWorld(World world) {
        this.activeWorld = world;
        this.lobbyScoreboard.updateMap(world.getName());
        this.activeWorld.setGameRuleValue("keepInventory", "true");
    }

    public World getActiveWorld() {
        return this.activeWorld;
    }

    public void voteForMap(Player player, String mapName) {
        UUID playerUUID = player.getUniqueId();
        // Prüfen, ob der Spieler bereits für diese Map gestimmt hat
        if (playerVotes.containsKey(playerUUID) && playerVotes.get(playerUUID).equals(mapName)) {
            return;
        }

        // Wenn der Spieler zuvor für eine andere Map gestimmt hat, entferne diesen Vote
        if (playerVotes.containsKey(playerUUID)) {
            String previousVote = playerVotes.get(playerUUID);
            removeVote(previousVote);
        }

        // Aktualisiere die Wahl des Spielers und füge einen Vote für die neue Map hinzu
        playerVotes.put(playerUUID, mapName);
        addVote(mapName);
        selectMapWithMostVotes();
    }


    public void addVote(String mapName) {
        mapVotes.merge(mapName, 1, Integer::sum);
    }

    public void removeVote(String mapName) {
        mapVotes.computeIfPresent(mapName, (key, val) -> val > 1 ? val - 1 : 0);
    }

    public void selectMapWithMostVotes() {
        String mapWithMostVotes = Collections.max(mapVotes.entrySet(), Map.Entry.comparingByValue()).getKey();
        setActiveWorld(Bukkit.getWorld(mapWithMostVotes));
    }
}
