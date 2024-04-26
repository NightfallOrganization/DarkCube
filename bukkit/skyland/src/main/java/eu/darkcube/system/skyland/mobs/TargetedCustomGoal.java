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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;

public abstract class TargetedCustomGoal implements Goal<Mob> {

    protected final GoalKey<Mob> key;
    protected final Mob mob;
    protected Player closestPlayer;
    protected int cooldown;

    public TargetedCustomGoal(Plugin plugin, Mob mob) {
        this.key = GoalKey.of(Mob.class, new NamespacedKey(plugin, "zombieTestGoal"));
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

    }

    @Override public @NotNull GoalKey<Mob> getKey() {
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
