package eu.darkcube.system.miners.worldgen.minegen;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

public class MinePopulator extends BlockPopulator {

    SimplexOctaveGenerator islandGen = new SimplexOctaveGenerator(new Random(1), 8);

    public MinePopulator() {
        islandGen.setScale(1D);
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        int[][] coordinates = {
                {0, 80, 0},
                {0, 80, 10000},
                {0, 80, 20000},
                {0, 80, 30000},
                {0, 80, 40000},
                {0, 80, 50000},
                {0, 80, 60000},
                {0, 80, 70000},

        };

        for (int[] coord : coordinates) {
            createHollowArea(limitedRegion, coord[0], coord[1], coord[2]);
        }
    }

    private void createHollowArea(LimitedRegion region, int x, int y, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    if (region.isInRegion(x + dx, y + dy, z + dz)) {
                        region.setType(x + dx, y + dy, z + dz, Material.LIGHT);
                    }
                }
            }
        }
    }
}
