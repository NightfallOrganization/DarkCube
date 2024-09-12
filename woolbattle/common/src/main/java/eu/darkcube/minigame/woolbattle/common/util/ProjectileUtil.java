/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util;

import java.util.Random;

import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;

public class ProjectileUtil {
    public static final double SPREAD_MULTIPLIER = 0.007499999832361937;

    public static Vector shootVelocity(Random random, Location fromLocation, float strength, float spread) {
        var direction = fromLocation.direction();
        var spreadVelocity = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).mul(SPREAD_MULTIPLIER).mul(spread);
        var velocity = direction.plus(spreadVelocity);
        return velocity.mul(strength);
    }
}
