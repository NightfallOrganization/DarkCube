/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.mobs;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import eu.darkcube.system.bukkit.Plugin;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;

public class ZombieGoal implements Goal<Villager> {

    private final GoalKey<Villager> key;
    private final Mob mob;
    private Player closestPlayer;
    private int cooldown;

    public ZombieGoal(Plugin plugin, Mob mob) {
        this.key = GoalKey.of(Villager.class, new NamespacedKey(plugin, "zombieTestGoal"));
        this.mob = mob;
    }

    @Override public boolean shouldActivate() {
        if (cooldown > 0) {
            --cooldown;
            return false;
        }
        closestPlayer = getClosestPlayer();
        return closestPlayer != null;
    }

    @Override public boolean shouldStayActive() {
        return shouldActivate();
    }

    @Override public void start() {
    }

    @Override public void stop() {
        mob.getPathfinder().stopPathfinding();
        mob.setTarget(null);
        cooldown = 100;
    }

    @Override public void tick() {
        mob.setTarget(closestPlayer);
        if (mob.getLocation().distanceSquared(closestPlayer.getLocation()) < 6.25) {
            mob.getPathfinder().stopPathfinding();
        } else {
            mob.getPathfinder().moveTo(mob.getLocation().add(10, -1, 0), 1.0D);
        }
    }

    @Override public @NotNull GoalKey<Villager> getKey() {
        return key;
    }

    @Override public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }

    private Player getClosestPlayer() {
        Collection<Player> nearbyPlayers = mob
                .getWorld()
                .getNearbyPlayers(mob.getLocation(), 10.0, player -> !player.isDead() && player.getGameMode() != GameMode.SPECTATOR && player.isValid());
        double closestDistance = -1.0;
        Player closestPlayer = null;
        for (Player player : nearbyPlayers) {
            double distance = player.getLocation().distanceSquared(mob.getLocation());
            if (closestDistance != -1.0 && !(distance < closestDistance)) {
                continue;
            }
            closestDistance = distance;
            closestPlayer = player;
        }
        return closestPlayer;
    }
}
