package eu.darkcube.minigame.woolbattle.minestom.util;

import eu.darkcube.minigame.woolbattle.api.world.Location;
import net.minestom.server.coordinate.Pos;

public class PosUtil {
    public static Pos toPos(Location loc) {
        return new Pos(loc.x(), loc.y(), loc.z(), loc.yaw(), loc.pitch());
    }
}
