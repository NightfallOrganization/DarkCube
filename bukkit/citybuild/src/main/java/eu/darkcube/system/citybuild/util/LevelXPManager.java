/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.Citybuild;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelXPManager implements Listener {

    private JavaPlugin plugin;
    private ScoreboardManager scoreboardManager;

    public LevelXPManager(JavaPlugin plugin, CorManager corManager) {
        this.plugin = plugin;
        this.scoreboardManager = new ScoreboardManager((Citybuild) plugin, this, corManager);
    }


    public int getXPForLevel(int level) {
        level = Math.max(1, level);
        int xp = (int) Math.pow(level, 2);  // Die benötigten XP steigen exponentiell mit dem Level an.
        return Math.max(1, xp);
    }

    public void addXP(Player player, double amount) {
        NamespacedKey xpKey = new NamespacedKey(plugin, "xp");
        NamespacedKey levelKey = new NamespacedKey(plugin, "level");
        NamespacedKey apKey = new NamespacedKey(plugin, "ap"); // Neuer AP Schlüssel

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        double currentXP = pdc.getOrDefault(xpKey, PersistentDataType.DOUBLE, 0.0);
        int currentLevel = pdc.getOrDefault(levelKey, PersistentDataType.INTEGER, 0);
        int currentAP = pdc.getOrDefault(apKey, PersistentDataType.INTEGER, 0); // Aktuelle AP erhalten
        double newXP = currentXP + amount;

        boolean levelUp = false; // Track if the player leveled up

        while (newXP >= getXPForLevel(currentLevel + 1)) {
            newXP -= getXPForLevel(currentLevel + 1);
            currentLevel++;
            currentAP += 2; // AP um 2 erhöhen, wenn Level erhöht wird

            levelUp = true;
        }

        // Setze die aktualisierten Daten zurück in den PersistentDataContainer
        pdc.set(xpKey, PersistentDataType.DOUBLE, newXP);
        pdc.set(levelKey, PersistentDataType.INTEGER, currentLevel);
        pdc.set(apKey, PersistentDataType.INTEGER, currentAP);

        // Wenn der Spieler ein Level-up hatte, aktualisiere das Scoreboard und sende Benachrichtigungen
        if (levelUp) {
            player.sendTitle("§6Level: §e" + currentLevel, "", 10, 70, 20);
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1f, 1f);

            scoreboardManager.updatePlayerLevel(player); // Rufen Sie die Methode auf, die wir zuvor hinzugefügt haben
        }
    }


    public void resetXP(Player player) {
        NamespacedKey xpKey = new NamespacedKey(plugin, "xp");
        NamespacedKey levelKey = new NamespacedKey(plugin, "level");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(xpKey, PersistentDataType.DOUBLE, 0.0);
        pdc.set(levelKey, PersistentDataType.INTEGER, 0);
        player.setExp(0);
        player.setLevel(0);
    }

    public void resetAP(Player player) {
        NamespacedKey apKey = new NamespacedKey(plugin, "ap");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(apKey, PersistentDataType.INTEGER, 0);
    }

    public double getXP(Player player) {
        NamespacedKey xpKey = new NamespacedKey(plugin, "xp");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(xpKey, PersistentDataType.DOUBLE, 0.0);
    }

    public int getAP(Player player) {
        NamespacedKey apKey = new NamespacedKey(plugin, "ap");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(apKey, PersistentDataType.INTEGER, 0);
    }

    public void addAP(Player player, int amount) {
        NamespacedKey apKey = new NamespacedKey(plugin, "ap");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        int currentAP = pdc.getOrDefault(apKey, PersistentDataType.INTEGER, 0);
        int newAP = currentAP + amount;

        // Sicherstellen, dass AP nicht unter 0 fallen
        newAP = Math.max(0, newAP);

        pdc.set(apKey, PersistentDataType.INTEGER, newAP);
    }

    public void resetAttribute(Player player, String attributeKey) {
        NamespacedKey key = new NamespacedKey(plugin, attributeKey);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, 0.0);
    }

    public double getAttribute(Player player, String attributeKey) {
        NamespacedKey key = new NamespacedKey(plugin, attributeKey);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
    }

    public void setAttribute(Player player, String attributeKey, double value) {
        NamespacedKey key = new NamespacedKey(plugin, attributeKey);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, value);
    }

    public int getLevel(Player player) {
        NamespacedKey levelKey = new NamespacedKey(plugin, "level");
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(levelKey, PersistentDataType.INTEGER, 0);
    }

}
