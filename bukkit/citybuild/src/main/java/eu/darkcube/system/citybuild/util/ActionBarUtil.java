/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.Citybuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarUtil {

    private final Citybuild citybuild;
    private final CustomHealthManager healthManager;
    private final LevelXPManager levelXPManager;

    public ActionBarUtil(Citybuild citybuild, CustomHealthManager healthManager, LevelXPManager levelXPManager) {
        this.citybuild = citybuild;
        this.healthManager = healthManager;
        this.levelXPManager = levelXPManager;
        new BukkitRunnable() {
            @Override public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateActionbar(player);
                }
            }
        }.runTaskTimer(citybuild, 0, 3);
    }

    public void updateActionbar(Player player) {
        int xp = (int) levelXPManager.getXP(player);
        int maxXp = levelXPManager.getXPForLevel(levelXPManager.getLevel(player) + 1);
        int level = levelXPManager.getLevel(player);
        int health = healthManager.getHealth(player);
        int maxHealth = healthManager.getMaxHealth(player);

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
        return citybuild.glyphWidthManager().width(PlainTextComponentSerializer.plainText().serialize(component));
    }

    private Component createWidthOffsetComponent(float width) {
        return Component.text(citybuild.glyphWidthManager().spacesForWidth(width));
    }

    // Hilfsmethode zum Ersetzen von Zahlen durch Buchstaben
    private String replaceNumbersWithLetters(String input) {
        return input.replace('1', 'Ḓ')
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
