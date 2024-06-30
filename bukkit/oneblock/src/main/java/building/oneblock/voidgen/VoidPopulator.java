package building.oneblock.voidgen;


import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class VoidPopulator extends BlockPopulator {

    SimplexOctaveGenerator islandGen = new SimplexOctaveGenerator(new Random(1), 8);

    public VoidPopulator() {
        islandGen.setScale(1D);
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
                         int chunkZ, @NotNull LimitedRegion limitedRegion) {

    }





}