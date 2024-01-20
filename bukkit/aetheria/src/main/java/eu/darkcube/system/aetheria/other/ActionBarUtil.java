/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.other;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.handler.LevelXPHandler;
import eu.darkcube.system.aetheria.manager.player.HealthManager;
import eu.darkcube.system.aetheria.manager.player.LevelManager;
import eu.darkcube.system.aetheria.manager.player.MaxHealthManager;
import eu.darkcube.system.aetheria.manager.player.XPManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarUtil {

    private final Aetheria aetheria;
    private final HealthManager healthManager;
    private final LevelXPHandler levelXPHandler;
    private LevelManager levelManager;
    private MaxHealthManager maxHealthManager;
    private XPManager xpManager;

    public ActionBarUtil(Aetheria aetheria, HealthManager healthManager, LevelXPHandler levelXPManager, LevelManager levelManager, MaxHealthManager maxHealthManager, XPManager xpManager) {
        this.aetheria = aetheria;
        this.healthManager = healthManager;
        this.levelXPHandler = levelXPManager;
        this.levelManager = levelManager;
        this.maxHealthManager = maxHealthManager;
        this.xpManager = xpManager;
        new BukkitRunnable() {
            @Override public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateActionbar(player);
                }
            }
        }.runTaskTimer(aetheria, 0, 3);
    }

    public void updateActionbar(Player player) {
        int xp = (int) xpManager.getXP(player);
        int maxXp = (int) LevelXPHandler.calculateXPRequiredForNextLevel(levelManager.getLevel(player));
        int level = levelManager.getLevel(player);
        int health = (int) healthManager.getHealth(player);
        int maxHealth = (int) maxHealthManager.getMaxHealth(player);

        // Zahlen durch Buchstaben ersetzen
        String healthText = replaceNumbersWithLetters(String.valueOf(health));
        String maxHealthText = replaceNumbersWithLetters(String.valueOf(maxHealth));
        String xpText = replaceNumbersWithLetters(String.valueOf(xp));
        String maxXpText = replaceNumbersWithLetters(String.valueOf(maxXp));

        Component overlay = Component.text("Ⲓ", TextColor.fromHexString("#4e5c24"));

        Component message = Component
                .empty()
                .append(Component.text("Ḑ ").color(TextColor.fromHexString("#ff4c4c")))
                .append(Component.text(healthText).color(TextColor.fromHexString("#ff4c4c")))
                .append(Component.text("ḛ").color(TextColor.fromHexString("#7e7e7e")))
                .append(Component.text(maxHealthText).color(TextColor.fromHexString("#ff4c4c")))
                .append(Component.text(" ḝḞ").color(TextColor.fromHexString("#ff4c4c")))
                .append(Component.text(" Ḝ ").color(TextColor.fromHexString("#7e7e7e")))
                .append(Component.text("ḑ ").color(TextColor.fromHexString("#54ff50")))
                .append(Component.text(xpText).color(TextColor.fromHexString("#54ff50")))
                .append(Component.text("ḛ").color(TextColor.fromHexString("#7e7e7e")))
                .append(Component.text(maxXpText).color(TextColor.fromHexString("#54ff50")))
                .append(Component.text(" ḟḞ").color(TextColor.fromHexString("#54ff50")));

        player.sendActionBar(createActionbar(message, overlay));
    }

    private Component createActionbar(Component message, Component... overlays) {
        if (overlays.length == 0) return message;
        float messageWidth = width(message);
        float half = messageWidth / 2;
        half = (float) Math.ceil(half) - .5F;

        message = message.append(createWidthOffsetComponent(-half));
        for (Component overlay : overlays) {
            Component offset = createWidthOffsetComponent(-width(overlay) / 2);
            message = message.append(offset).append(overlay).append(offset);
        }
        message = message.append(createWidthOffsetComponent(half));
        return message;
    }

    private float width(Component component) {
        return aetheria.glyphWidthManager().width(PlainTextComponentSerializer.plainText().serialize(component));
    }

    private Component createWidthOffsetComponent(float width) {
        return Component.text(aetheria.glyphWidthManager().spacesForWidth(width));
    }

    // Hilfsmethode zum Ersetzen von Zahlen durch Buchstaben
    private String replaceNumbersWithLetters(String input) {
        return input
                .replace('1', 'Ḓ')
                .replace('2', 'ḓ')
                .replace('3', 'Ḕ')
                .replace('4', 'ḕ')
                .replace('5', 'Ḗ')
                .replace('6', 'ḗ')
                .replace('7', 'Ḙ')
                .replace('8', 'ḙ')
                .replace('9', 'Ḛ')
                .replace('0', 'Ḡ');
    }
}
