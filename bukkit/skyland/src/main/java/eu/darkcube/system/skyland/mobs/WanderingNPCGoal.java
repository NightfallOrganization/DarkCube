/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.mobs;

import eu.darkcube.system.bukkit.Plugin;
import org.bukkit.entity.Mob;

public class WanderingNPCGoal extends TargetedCustomGoal {

    public WanderingNPCGoal(Plugin plugin, Mob mob) {
        super(plugin, mob);
    }

    @Override public boolean shouldActivate() {
        return true;
    }

    @Override public void tick() {
        mob.setTarget(closestPlayer);
        if (mob.getLocation().distanceSquared(closestPlayer.getLocation()) < 6.25) {
            mob.getPathfinder().stopPathfinding();
        } else {
            mob.getPathfinder().moveTo(mob.getLocation().add(10, -1, 0));
        }
    }
}
