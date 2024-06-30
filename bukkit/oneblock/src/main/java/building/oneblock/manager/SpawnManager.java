package building.oneblock.manager;

import org.bukkit.Location;
import org.bukkit.World;
import static building.oneblock.manager.WorldManager.SPAWN;

public class SpawnManager {
    private static final double SPAWN_X = 0.5;
    private static final double SPAWN_Y = 100;
    private static final double SPAWN_Z = 0.5;
    private static final float YAW = 0;
    private static final float PITCH = 0;

    public Location getSpawnLocation() {
        World spawnWorld = SPAWN;
        return new Location(spawnWorld, SPAWN_X, SPAWN_Y, SPAWN_Z, YAW, PITCH);
    }

}
