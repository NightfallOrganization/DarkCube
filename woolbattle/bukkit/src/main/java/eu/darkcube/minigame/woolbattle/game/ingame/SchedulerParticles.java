/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game.ingame;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class SchedulerParticles extends Scheduler implements ConfiguredScheduler {
    @Override
    public void run() {
        Collection<Player> particlePlayers =
                WBUser.onlineUsers().stream().filter(WBUser::particles).map(WBUser::getBukkitEntity)
                        .collect(Collectors.toList());
        for (World world : Bukkit.getWorlds()) {
            for (Arrow arrow : world.getEntitiesByClass(Arrow.class)) {
                if (arrow.hasMetadata("noParticles")) {
                    continue;
                }
                Location loc = arrow.getLocation();
                loc.add(arrow.getVelocity().multiply(10));
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        Location l = arrow.getLocation();
                        l.add(x * 16, 0, z * 16);
                        l.getChunk().load();
                    }
                }
                if (arrow.getShooter() instanceof Player) {
                    if (arrow.isDead() || arrow.isOnGround() || !arrow.isValid()
                            || !((Player) arrow.getShooter()).isOnline() || !arrow.getLocation()
                            .getChunk().isLoaded()) {
                        arrow.remove();
                        continue;
                    }
                    WBUser user = WBUser.getUser(((Player) arrow.getShooter()));
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.WOOL,
                                    user.getTeam().getType().getWoolColor().getWoolData()), 0, 0, 0, 1, 5,
                            arrow.getLocation(), particlePlayers);
                }
            }
        }
    }

    @Override
    public void start() {
        runTaskTimer(1);
    }

    @Override
    public void stop() {
        cancel();
    }
}
