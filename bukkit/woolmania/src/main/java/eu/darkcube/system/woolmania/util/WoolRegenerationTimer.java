/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import static eu.darkcube.system.woolmania.enums.Hallpools.HALLPOOL1;
import static eu.darkcube.system.woolmania.enums.Locations.HALL1;
import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WoolRegenerationTimer extends BukkitRunnable {
    private final JavaPlugin plugin;
    private static boolean isRunning = false;

    public WoolRegenerationTimer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Aufgabe, die nach 30 Minuten ausgeführt werden soll
        Bukkit.getServer().broadcastMessage("§730 Minuten sind vorbei!");
        RegenerateWoolAreas.regenerateWoolAreas();

        // Timer zurücksetzen
        isRunning = false;
        startTimerIfNotRunning(plugin);
    }

    public static void startTimerIfNotRunning(JavaPlugin plugin) {
        if (!isRunning) {
            isRunning = true;
            new WoolRegenerationTimer(plugin).runTaskLater(plugin, 30 * 60 * 20L); // 30 Minuten in Ticks
        }
    }

}
