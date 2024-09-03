/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.pvpphase;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;
import eu.darkcube.system.miners.util.Timer;

public class PVPPhase {

    public static World PVP_WORLD;

    private final Spawns spawns;
    private WorldBorder worldBorder;
    private Timer pvpTimer;

    public PVPPhase() {
        PVP_WORLD = Bukkit.getWorld(Miners.PVP_WORLD_NAME);
        spawns = new Spawns(PVP_WORLD);
        worldBorder = PVP_WORLD.getWorldBorder();
        worldBorder.setCenter(PVP_WORLD.getSpawnLocation());
        worldBorder.setSize(200);
        worldBorder.setDamageBuffer(0);
        worldBorder.setDamageAmount(1);
        worldBorder.setWarningDistance(0);
        worldBorder.setWarningTime(10);

        PVP_WORLD.setGameRuleValue("naturalRegeneration", "true");
        PVP_WORLD.setSpawnLocation(0, 100, 0);

        pvpTimer = new Timer() {
            private int[] notifications = {
                    240,
                    180,
                    120,
                    60,
                    30,
                    10
            };
            private int nextNotification = 0;

            @Override
            public void onIncrement() {
                int remainingSeconds = (int) Math.ceil(getTimeRemainingMillis() / 1000);
                if (nextNotification < notifications.length && remainingSeconds == notifications[nextNotification]) {
                    sendTimeRemaining(notifications[nextNotification]);
                    nextNotification++;
                }
            }

            @Override
            public void onEnd() {
//				Bukkit.getOnlinePlayers().forEach(p -> {
//					if (Miners.getTeamManager().getPlayerTeam(p) != 0)
//						p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 2, true, true), true);
//				});
            }

            private void sendTimeRemaining(int secs) {
                if (secs > 60) {
                    int mins = secs / 60;
                    Bukkit.getOnlinePlayers().forEach(p -> Miners.sendTranslatedMessage(p, Message.TIME_REMAINING, mins, Message.TIME_MINUTES));
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> Miners.sendTranslatedMessage(p, Message.TIME_REMAINING, secs, Message.TIME_SECONDS));
                }
            }
        };
    }

    public void enable() {
        for (int i = 1; i <= Miners.getMinersConfig().TEAM_COUNT; i++) {
            Location l = spawns.getRandomSpawn();
            Miners.getTeamManager().getPlayersInTeam(i).forEach(p -> p.teleport(l));
        }
        Miners.getTeamManager().getPlayersWithoutTeam().forEach(p -> p.teleport(PVP_WORLD.getSpawnLocation()));
        worldBorder.setSize(0, Miners.getMinersConfig().PVP_PHASE_DURATION);

        pvpTimer.start(Miners.getMinersConfig().PVP_PHASE_DURATION);
    }

    public void disable() {
        pvpTimer.cancel(false);
    }

    public Timer getTimer() {
        return pvpTimer;
    }

}
