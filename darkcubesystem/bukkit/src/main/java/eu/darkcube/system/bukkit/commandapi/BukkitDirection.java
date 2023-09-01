/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.commandapi.v3.Direction;
import eu.darkcube.system.commandapi.v3.MathHelper;
import org.bukkit.entity.Entity;

import static eu.darkcube.system.commandapi.v3.Direction.*;

public final class BukkitDirection {
    public static Direction[] getFacingDirections(Entity entityIn) {
        float f = entityIn.getLocation().getPitch() * ((float) Math.PI / 180F);
        float f1 = -entityIn.getLocation().getYaw() * ((float) Math.PI / 180F);
        float f2 = MathHelper.sin(f);
        float f3 = MathHelper.cos(f);
        float f4 = MathHelper.sin(f1);
        float f5 = MathHelper.cos(f1);
        boolean flag = f4 > 0.0F;
        boolean flag1 = f2 < 0.0F;
        boolean flag2 = f5 > 0.0F;
        float f6 = flag ? f4 : -f4;
        float f7 = flag1 ? -f2 : f2;
        float f8 = flag2 ? f5 : -f5;
        float f9 = f6 * f3;
        float f10 = f8 * f3;
        Direction direction = flag ? EAST : WEST;
        Direction direction1 = flag1 ? UP : DOWN;
        Direction direction2 = flag2 ? SOUTH : NORTH;
        if (f6 > f8) {
            if (f7 > f9) {
                return compose(direction1, direction, direction2);
            }
            return f10 > f7 ? compose(direction, direction2, direction1) : compose(direction, direction1, direction2);
        } else if (f7 > f10) {
            return compose(direction1, direction2, direction);
        } else {
            return f9 > f7 ? compose(direction2, direction, direction1) : compose(direction2, direction1, direction);
        }
    }

    private static Direction[] compose(Direction first, Direction second, Direction third) {
        return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
    }
}
