/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.Citybuild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final Citybuild plugin;
    private final LevelXPManager levelManager;

    public ScoreboardManager(Citybuild plugin, LevelXPManager levelManager) {
        this.plugin = plugin;
        this.levelManager = levelManager;
    }

    private Map<UUID, Objective> playerObjectives = new HashMap<>();

    public void createScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("playerStats", "dummy", ChatColor.GRAY + "« " + ChatColor.DARK_GREEN + "Dark" + ChatColor.GREEN + "Cube" + ChatColor.GRAY + ".eu »");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Header
        Score headerSpacer = obj.getScore(" "); // Lücke
        headerSpacer.setScore(10);

        // Gilde
        Score gilde = obj.getScore(ChatColor.GRAY + "➥ " + ChatColor.GREEN + "Gilde:");
        gilde.setScore(9);
        Score gildenname = obj.getScore(ChatColor.GRAY + "» " + getGildenNameForPlayer(player));
        gildenname.setScore(8);

        // Lücke zwischen Gilde und Cor
        Score gildeToCorSpacer = obj.getScore("  ");
        gildeToCorSpacer.setScore(7);

        // Cor
        Score cor = obj.getScore(ChatColor.GRAY + "➥ " + ChatColor.GREEN + "Cor:");
        cor.setScore(6);
        Score betrag = obj.getScore(ChatColor.GRAY + "» " + getBetragForPlayer(player));
        betrag.setScore(5);

        // Lücke zwischen Cor und Level
        Score corToLevelSpacer = obj.getScore("   ");
        corToLevelSpacer.setScore(4);

        // Level
        Score level = obj.getScore(ChatColor.GRAY + "➥ " + ChatColor.GREEN + "Level:");
        level.setScore(3);
        int playerLevel = levelManager.getLevel(player); // Spielerlevel holen
        Score lvl = obj.getScore(ChatColor.GRAY + "» " + playerLevel);
        lvl.setScore(2);

        playerObjectives.put(player.getUniqueId(), obj);
        player.setScoreboard(board);
    }

    public void updatePlayerLevel(Player player) {
        Objective obj = playerObjectives.get(player.getUniqueId());
        if (obj != null) {
            int playerLevel = levelManager.getLevel(player);
            // Das Score-Objekt für die Level-Zeile abrufen und aktualisieren
            Score lvl = obj.getScore(ChatColor.GRAY + "» " + playerLevel);
            lvl.setScore(2); // Hier ist 2 der Platz des Levels auf dem Scoreboard. Ändere dies entsprechend, wenn es sich ändert.
        }
    }

    // Beispiel-Methoden, du musst diese mit den tatsächlichen Daten ersetzen
    private String getGildenNameForPlayer(Player player) {
        // Hole den Gildenname des Spielers
        return "[Gildenname]";
    }

    private String getBetragForPlayer(Player player) {
        // Hole den Betrag des Spielers
        return "[Betrag]";
    }

    private String getLevelForPlayer(Player player) {
        // Hole das Level des Spielers
        return "[LvL]";
    }

    private String getTimeForBooster(Player player) {
        // Hole die Booster-Zeit des Spielers
        return "[Time]";
    }

    private String getCombatTimerForPlayer(Player player) {
        // Hole den Combat-Timer des Spielers
        return "[Timer]";
    }
}
