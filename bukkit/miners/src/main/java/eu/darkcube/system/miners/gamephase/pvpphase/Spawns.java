package eu.darkcube.system.miners.gamephase.pvpphase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;

public class Spawns {

    private List<Location> spawns;

    private final Random RANDOM = new Random();

    public Spawns(World world) {
        spawns = new ArrayList<>();
        spawns.add(new Location(world, 50, 100, 50));
        spawns.add(new Location(world, 50, 100, 0));
        spawns.add(new Location(world, 0, 100, 50));

        spawns.add(new Location(world, -50, 100, 50));
        spawns.add(new Location(world, 50, 100, -50));

        spawns.add(new Location(world, -50, 100, -50));
        spawns.add(new Location(world, -50, 100, 0));
        spawns.add(new Location(world, 0, 100, -50));
    }

    public Location getRandomSpawn() {
        Location l = spawns.get(RANDOM.nextInt(spawns.size()));
        spawns.remove(l);
        return l;
    }

}
