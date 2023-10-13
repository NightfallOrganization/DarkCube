/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.game;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import static eu.darkcube.system.woolbattleteamfight.lobby.LobbyItemManager.playersWithParticlesOff;

public class ArrowManager implements Listener {

    private Plugin plugin;

    public ArrowManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            arrow.setCritical(false);  // Verhindert kritische Partikel

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (arrow.isDead() || arrow.isOnGround()) {
                        this.cancel();
                        return;
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!playersWithParticlesOff.contains(p)) { // Überprüfen, ob der Spieler die Partikel nicht ausgeschaltet hat
                            for (int j = 0; j < 3; j++) {
                                for (int i = 0; i < 5; i++) {
                                    double offsetX = (Math.random() - 0.2) / 20.0;
                                    double offsetY = (Math.random() - 0.2) / 20.0 + 0.2;
                                    double offsetZ = (Math.random() - 0.2) / 20.0;

                                    p.spigot().playEffect(arrow.getLocation().add(offsetX, offsetY, offsetZ), Effect.SMALL_SMOKE, 0, 4, 0.1F, 0.1F, 0.1F, 0.1F, 1, 100);
                                }
                            }
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            arrow.remove();
        }
    }
}
