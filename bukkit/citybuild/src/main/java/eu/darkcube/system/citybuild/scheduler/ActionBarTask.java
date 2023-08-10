/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.scheduler;

import eu.darkcube.system.citybuild.listener.Citybuild;
import eu.darkcube.system.citybuild.util.CustomHealthManager;
import eu.darkcube.system.citybuild.util.LevelXPManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {

    private final Citybuild citybuild;
    private final CustomHealthManager healthManager;
    private final LevelXPManager levelXPManager;

    public ActionBarTask(Citybuild citybuild, CustomHealthManager healthManager, LevelXPManager levelXPManager) {
        this.citybuild = citybuild;
        this.healthManager = healthManager;
        this.levelXPManager = levelXPManager;
    }

    @Override public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            int xp = (int) levelXPManager.getXP(player);
            int maxXp = levelXPManager.getXPForLevel(levelXPManager.getLevel(player) + 1);
            int level = levelXPManager.getLevel(player);
            int health = healthManager.getHealth(player);
            int maxHealth = healthManager.getMaxHealth(player);

            Component overlay = Component.text("Ⲓ", TextColor.fromHexString("#4e5c24"));

            Component message = Component
                    .empty()
                    .append(Component.text("❤ ").color(TextColor.fromHexString("#ff4c4c")))
                    .append(Component.text(health).color(TextColor.fromHexString("#ff4c4c")))
                    .append(Component.text("/").color(TextColor.fromHexString("#7e7e7e")))
                    .append(Component.text(maxHealth).color(TextColor.fromHexString("#ff4c4c")))
                    .append(Component.text(" LP").color(TextColor.fromHexString("#ff4c4c")))
                    .append(Component.text(" ¦ ").color(TextColor.fromHexString("#7e7e7e")))
                    .append(Component.text("✦ ").color(TextColor.fromHexString("#54ff50")))
                    .append(Component.text(xp).color(TextColor.fromHexString("#54ff50")))
                    .append(Component.text("/").color(TextColor.fromHexString("#7e7e7e")))
                    .append(Component.text(maxXp).color(TextColor.fromHexString("#54ff50")))
                    .append(Component.text(" EP").color(TextColor.fromHexString("#54ff50")));

            player.sendActionBar(createActionbar(message, overlay));
        }
    }

    private Component createActionbar(Component message, Component... overlays) {
        if (overlays.length == 0) return message;
        float messageWidth = width(message);
        message = message.append(createWidthOffsetComponent(-messageWidth / 2));
        for (Component overlay : overlays) {
            Component offset = createWidthOffsetComponent(-width(overlay) / 2);
            message = message.append(offset).append(overlay).append(offset);
        }
        message = message.append(createWidthOffsetComponent(messageWidth / 2));
        return message;
    }

    private float width(Component component) {
        return citybuild.glyphWidthManager().width(PlainTextComponentSerializer.plainText().serialize(component));
    }

    private Component createWidthOffsetComponent(float width) {
        return Component.text(citybuild.glyphWidthManager().spacesForWidth(width));
    }
}
