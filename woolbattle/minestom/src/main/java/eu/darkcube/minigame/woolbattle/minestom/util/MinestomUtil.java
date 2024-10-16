/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.util;

import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class MinestomUtil {
    public static Pos toPos(Location loc) {
        return new Pos(loc.x(), loc.y(), loc.z(), loc.yaw(), loc.pitch());
    }

    public static Vec toVec(Vector vector) {
        return new Vec(vector.x(), vector.y(), vector.z());
    }

    public static Vector toVec(Vec vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static Vec toVelocity(Vector vector) {
        return toVec(vector).mul(ServerFlag.SERVER_TICKS_PER_SECOND);
    }

    public static Vector toVelocity(Vec vec) {
        return toVec(vec).div(ServerFlag.SERVER_TICKS_PER_SECOND);
    }
}
